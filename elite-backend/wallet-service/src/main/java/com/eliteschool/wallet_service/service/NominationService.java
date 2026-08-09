package com.eliteschool.wallet_service.service;

import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.wallet_service.dto.NominationDto;
import com.eliteschool.wallet_service.dto.NominationMapper;
import com.eliteschool.wallet_service.model.ContributionNomination;
import com.eliteschool.wallet_service.model.enums.NominationStatus;
import com.eliteschool.wallet_service.model.enums.TransactionSource;
import com.eliteschool.wallet_service.repository.ContributionNominationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NominationService {

    private final ContributionNominationRepository nominationRepository;
    private final WalletService walletService;

    @Transactional
    public NominationDto create(NominationDto dto, UUID nominatorId, String nominatorRole) {
        if (dto.getReason() == null || dto.getReason().isBlank()) {
            throw new AppException("Reason required", "Describe the contribution being nominated",
                    "INVALID_NOMINATION", HttpStatus.BAD_REQUEST);
        }
        if (dto.getSuggestedPoints() == null || dto.getSuggestedPoints() < 1) {
            throw new AppException("Points required", "Suggested points must be at least 1",
                    "INVALID_NOMINATION", HttpStatus.BAD_REQUEST);
        }

        ContributionNomination nomination = ContributionNomination.builder()
                .studentId(dto.getStudentId())
                .nominatedBy(nominatorId)
                .nominatorRole(nominatorRole != null ? nominatorRole.toUpperCase(Locale.ROOT) : null)
                .suggestedPoints(dto.getSuggestedPoints())
                .reason(dto.getReason().trim())
                .evidenceNote(dto.getEvidenceNote())
                .status(NominationStatus.PENDING)
                .build();

        ContributionNomination saved = nominationRepository.save(nomination);
        log.info("Nomination {} created by {} for student {}", saved.getId(), nominatorId, saved.getStudentId());
        return NominationMapper.toDto(saved);
    }

    public List<NominationDto> listForCaller(String role, UUID userId, NominationStatus status) {
        String r = role != null ? role.toUpperCase(Locale.ROOT) : "";
        if ("ADMIN".equals(r) || "MANAGEMENT".equals(r)) {
            if (status != null) {
                return NominationMapper.toDtoList(nominationRepository.findByStatusOrderByCreatedAtDesc(status));
            }
            return NominationMapper.toDtoList(nominationRepository.findAllByOrderByCreatedAtDesc());
        }
        List<ContributionNomination> mine = nominationRepository.findByNominatedByOrderByCreatedAtDesc(userId);
        if (status != null) {
            return NominationMapper.toDtoList(mine.stream().filter(n -> n.getStatus() == status).toList());
        }
        return NominationMapper.toDtoList(mine);
    }

    @Transactional
    public NominationDto approve(UUID nominationId, UUID adminId, Integer pointsOverride, String reviewNotes) {
        ContributionNomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new AppException("Nomination not found", "Nomination not found",
                        "NOMINATION_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (nomination.getStatus() != NominationStatus.PENDING) {
            throw new AppException("Not pending", "Only pending nominations can be approved",
                    "INVALID_NOMINATION_STATUS", HttpStatus.BAD_REQUEST);
        }

        int points = pointsOverride != null && pointsOverride > 0
                ? pointsOverride
                : nomination.getSuggestedPoints();

        String referenceId = "nomination-" + nomination.getId();
        String description = "Nomination approved: " + truncate(nomination.getReason(), 180);

        walletService.creditPoints(
                nomination.getStudentId(),
                points,
                description,
                referenceId,
                TransactionSource.NOMINATION);

        nomination.setStatus(NominationStatus.APPROVED);
        nomination.setReviewedBy(adminId);
        nomination.setReviewedAt(LocalDateTime.now());
        nomination.setReviewNotes(reviewNotes);
        nomination.setWalletReferenceId(referenceId);
        nomination.setAwardedPoints(points);

        return NominationMapper.toDto(nominationRepository.save(nomination));
    }

    @Transactional
    public NominationDto reject(UUID nominationId, UUID adminId, String reviewNotes) {
        ContributionNomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new AppException("Nomination not found", "Nomination not found",
                        "NOMINATION_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (nomination.getStatus() != NominationStatus.PENDING) {
            throw new AppException("Not pending", "Only pending nominations can be rejected",
                    "INVALID_NOMINATION_STATUS", HttpStatus.BAD_REQUEST);
        }

        nomination.setStatus(NominationStatus.REJECTED);
        nomination.setReviewedBy(adminId);
        nomination.setReviewedAt(LocalDateTime.now());
        nomination.setReviewNotes(reviewNotes);
        return NominationMapper.toDto(nominationRepository.save(nomination));
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
