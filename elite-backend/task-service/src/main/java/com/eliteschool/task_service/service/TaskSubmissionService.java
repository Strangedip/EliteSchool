package com.eliteschool.task_service.service;

import com.eliteschool.common_utils.exception.AppException;
import com.eliteschool.task_service.dto.CompletionCheckResponse;
import com.eliteschool.task_service.dto.TaskSubmissionDto;
import com.eliteschool.task_service.dto.TaskSubmissionMapper;
import com.eliteschool.task_service.model.Task;
import com.eliteschool.task_service.model.TaskSubmission;
import com.eliteschool.task_service.model.enums.TaskStatus;
import com.eliteschool.task_service.model.enums.TaskType;
import com.eliteschool.task_service.repository.TaskRepository;
import com.eliteschool.task_service.repository.TaskSubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskSubmissionService {

    private final TaskSubmissionRepository taskSubmissionRepository;
    private final TaskRepository taskRepository;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TaskSubmissionService(
            TaskSubmissionRepository taskSubmissionRepository,
            TaskRepository taskRepository,
            WebClient.Builder webClientBuilder) {
        this.taskSubmissionRepository = taskSubmissionRepository;
        this.taskRepository = taskRepository;
        this.webClientBuilder = webClientBuilder;
    }

    @Transactional
    public TaskSubmissionDto submitTask(TaskSubmissionDto submissionDto) {
        if (submissionDto.getTaskId() == null || submissionDto.getStudentId() == null) {
            throw new AppException("Task id and student id are required", "Invalid submission",
                    "INVALID_SUBMISSION", HttpStatus.BAD_REQUEST);
        }

        Task task = taskRepository.findById(submissionDto.getTaskId())
                .orElseThrow(() -> new AppException("Task not found", "Task not found",
                        "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (task.getStatus() != TaskStatus.OPEN) {
            throw new AppException("Task is not open for submissions", "This task is closed or already completed",
                    "TASK_NOT_OPEN", HttpStatus.BAD_REQUEST);
        }

        validateSubmissionStandards(task, submissionDto);

        Optional<TaskSubmission> existing = taskSubmissionRepository
                .findByTaskIdAndStudentId(submissionDto.getTaskId(), submissionDto.getStudentId());

        if (existing.isPresent()) {
            TaskSubmission prior = existing.get();
            if (prior.getStatus() == TaskStatus.SUBMITTED || prior.getStatus() == TaskStatus.COMPLETED) {
                throw new AppException("Already submitted", "You already have an active submission for this task",
                        "DUPLICATE_SUBMISSION", HttpStatus.CONFLICT);
            }
            prior.setSubmissionDetails(submissionDto.getSubmissionDetails());
            prior.setEvidence(submissionDto.getEvidence());
            prior.setRubricChecked(submissionDto.getRubricChecked() != null
                    ? new ArrayList<>(submissionDto.getRubricChecked())
                    : new ArrayList<>());
            prior.setStatus(TaskStatus.SUBMITTED);
            prior.setSubmittedAt(LocalDateTime.now());
            prior.setFeedbackNotes(null);
            prior.setVerifiedBy(null);
            prior.setVerifiedAt(null);
            prior.setPointsAwarded(false);
            TaskSubmission saved = taskSubmissionRepository.save(prior);
            return TaskSubmissionMapper.toDto(saved, task);
        }

        TaskSubmission submission = TaskSubmissionMapper.toEntity(submissionDto);
        submission.setStatus(TaskStatus.SUBMITTED);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setPointsAwarded(false);
        TaskSubmission saved = taskSubmissionRepository.save(submission);
        return TaskSubmissionMapper.toDto(saved, task);
    }

    @Transactional
    public Optional<TaskSubmissionDto> verifySubmission(UUID submissionId, UUID verifierId, boolean approved, String feedback) {
        Optional<TaskSubmission> submissionOpt = taskSubmissionRepository.findById(submissionId);
        if (submissionOpt.isEmpty()) {
            return Optional.empty();
        }

        TaskSubmission submission = submissionOpt.get();
        if (submission.getStatus() == TaskStatus.COMPLETED && submission.isPointsAwarded()) {
            throw new AppException("Already verified", "This submission was already approved and Elite Points were credited",
                    "ALREADY_VERIFIED", HttpStatus.CONFLICT);
        }
        if (submission.getStatus() != TaskStatus.SUBMITTED
                && !(submission.getStatus() == TaskStatus.COMPLETED && !submission.isPointsAwarded())) {
            throw new AppException("Submission is not pending verification",
                    "Only submitted work can be approved or rejected",
                    "INVALID_SUBMISSION_STATUS", HttpStatus.BAD_REQUEST);
        }

        Task task = taskRepository.findById(submission.getTaskId())
                .orElseThrow(() -> new AppException("Task not found", "Task not found",
                        "TASK_NOT_FOUND", HttpStatus.NOT_FOUND));

        if (!approved) {
            submission.setStatus(TaskStatus.REJECTED);
            submission.setVerifiedBy(verifierId);
            submission.setVerifiedAt(LocalDateTime.now());
            submission.setFeedbackNotes(feedback);
            submission.setPointsAwarded(false);
            TaskSubmission updated = taskSubmissionRepository.save(submission);
            return Optional.of(TaskSubmissionMapper.toDto(updated, task));
        }

        // Award first (idempotent via referenceId = submission id), then mark complete
        if (!submission.isPointsAwarded()) {
            awardPointsSync(submission.getStudentId(), task, submission.getId());
            submission.setPointsAwarded(true);
        }

        submission.setStatus(TaskStatus.COMPLETED);
        submission.setVerifiedBy(verifierId);
        submission.setVerifiedAt(LocalDateTime.now());
        submission.setFeedbackNotes(feedback);

        if (task.getTaskType() == TaskType.SINGLE) {
            task.setStatus(TaskStatus.COMPLETED);
            task.setCompletedBy(submission.getStudentId());
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        TaskSubmission updated = taskSubmissionRepository.save(submission);
        return Optional.of(TaskSubmissionMapper.toDto(updated, task));
    }

    public List<TaskSubmissionDto> getSubmissionsByTask(UUID taskId) {
        return enrich(taskSubmissionRepository.findByTaskId(taskId));
    }

    public List<TaskSubmissionDto> getSubmissionsByStudent(UUID studentId) {
        return enrich(taskSubmissionRepository.findByStudentId(studentId));
    }

    public List<TaskSubmissionDto> getSubmissionsByStatus(TaskStatus status) {
        return enrich(taskSubmissionRepository.findByStatus(status));
    }

    public CompletionCheckResponse checkCompletion(UUID studentId, List<UUID> taskIds) {
        if (studentId == null) {
            throw new AppException("Student id is required", "Student id is required",
                    "INVALID_REQUEST", HttpStatus.BAD_REQUEST);
        }
        List<UUID> ids = taskIds == null ? List.of() : taskIds.stream().filter(id -> id != null).distinct().toList();
        if (ids.isEmpty()) {
            return CompletionCheckResponse.builder().allCompleted(true).tasks(List.of()).build();
        }

        Map<UUID, Task> tasksById = taskRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        List<CompletionCheckResponse.TaskProgress> progress = new ArrayList<>();
        boolean allCompleted = true;
        for (UUID taskId : ids) {
            Task task = tasksById.get(taskId);
            boolean completed = taskSubmissionRepository.existsByTaskIdAndStudentIdAndStatus(
                    taskId, studentId, TaskStatus.COMPLETED);
            if (!completed) {
                allCompleted = false;
            }
            progress.add(CompletionCheckResponse.TaskProgress.builder()
                    .taskId(taskId)
                    .title(task != null ? task.getTitle() : "Unknown task")
                    .completed(completed)
                    .build());
        }

        return CompletionCheckResponse.builder()
                .allCompleted(allCompleted)
                .tasks(progress)
                .build();
    }

    private List<TaskSubmissionDto> enrich(List<TaskSubmission> submissions) {
        if (submissions.isEmpty()) {
            return List.of();
        }

        List<UUID> taskIds = submissions.stream()
                .map(TaskSubmission::getTaskId)
                .distinct()
                .toList();

        Map<UUID, Task> tasksById = taskRepository.findAllById(taskIds).stream()
                .collect(Collectors.toMap(Task::getId, Function.identity()));

        return TaskSubmissionMapper.toDtoList(submissions, tasksById);
    }

    private void validateSubmissionStandards(Task task, TaskSubmissionDto submissionDto) {
        String notes = submissionDto.getSubmissionDetails() != null
                ? submissionDto.getSubmissionDetails().trim()
                : "";
        int minNotes = Math.max(0, task.getMinNotesLength());
        if (notes.length() < minNotes) {
            throw new AppException(
                    "Notes too short",
                    "Submission notes must be at least " + minNotes + " characters",
                    "NOTES_TOO_SHORT",
                    HttpStatus.BAD_REQUEST);
        }

        if (task.isEvidenceRequired()) {
            String evidence = submissionDto.getEvidence() != null ? submissionDto.getEvidence().trim() : "";
            if (evidence.isEmpty()) {
                throw new AppException(
                        "Evidence required",
                        "This task requires evidence (link or description)",
                        "EVIDENCE_REQUIRED",
                        HttpStatus.BAD_REQUEST);
            }
        }

        List<String> rubric = task.getRubricChecklist() != null ? task.getRubricChecklist() : List.of();
        if (!rubric.isEmpty()) {
            List<String> checked = submissionDto.getRubricChecked() != null
                    ? submissionDto.getRubricChecked()
                    : List.of();
            for (String item : rubric) {
                if (item == null || item.isBlank()) {
                    continue;
                }
                boolean ok = checked.stream().anyMatch(c -> c != null && c.trim().equalsIgnoreCase(item.trim()));
                if (!ok) {
                    throw new AppException(
                            "Rubric incomplete",
                            "Please confirm all verification checklist items before submitting",
                            "RUBRIC_INCOMPLETE",
                            HttpStatus.BAD_REQUEST);
                }
            }
        }
    }

    private void awardPointsSync(UUID studentId, Task task, UUID submissionId) {
        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://points-service/api/points/award")
                    .header("e-internal-service", "task-service")
                    .bodyValue(new RewardRequest(
                            studentId,
                            task.getRewardPoints(),
                            "Task completion: " + task.getTitle(),
                            submissionId.toString()))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Failed to award points for submission {}: {}", submissionId, e.getMessage(), e);
            throw new AppException("Points award failed",
                    "Could not credit Elite Points. Try verifying again later.",
                    "POINTS_AWARD_FAILED", HttpStatus.BAD_GATEWAY);
        }
    }

    private record RewardRequest(UUID studentId, int points, String description, String referenceId) {}
}
