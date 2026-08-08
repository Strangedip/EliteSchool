package com.eliteschool.wallet_service.dto;

import com.eliteschool.wallet_service.model.ContributionNomination;

import java.util.List;
import java.util.stream.Collectors;

public final class NominationMapper {

    private NominationMapper() {}

    public static NominationDto toDto(ContributionNomination n) {
        if (n == null) {
            return null;
        }
        return NominationDto.builder()
                .id(n.getId())
                .studentId(n.getStudentId())
                .nominatedBy(n.getNominatedBy())
                .nominatorRole(n.getNominatorRole())
                .suggestedPoints(n.getSuggestedPoints())
                .reason(n.getReason())
                .evidenceNote(n.getEvidenceNote())
                .status(n.getStatus())
                .reviewedBy(n.getReviewedBy())
                .reviewedAt(n.getReviewedAt())
                .reviewNotes(n.getReviewNotes())
                .walletReferenceId(n.getWalletReferenceId())
                .awardedPoints(n.getAwardedPoints())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public static List<NominationDto> toDtoList(List<ContributionNomination> list) {
        if (list == null) {
            return List.of();
        }
        return list.stream().map(NominationMapper::toDto).collect(Collectors.toList());
    }
}
