import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Task, TaskSubmission, TaskTemplate } from '../../core/models/task.model';
import { TaskService } from '../../core/services/task.service';
import { UserService } from '../../core/services/user.service';
import { WalletService } from '../../core/services/wallet.service';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { Textarea } from 'primeng/textarea';
import { InputTextModule } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { InputNumber } from 'primeng/inputnumber';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { parseLines } from '../../core/utils/text.util';

type TaskFilterId = 'available' | 'review' | 'approved' | 'revision';

@Component({
    selector: 'app-taskboard',
    templateUrl: './taskboard.component.html',
    styleUrls: ['./taskboard.component.scss'],
    imports: [
    CommonModule,
    RouterModule,
    ButtonModule,
    FormsModule,
    DialogModule,
    Textarea,
    InputTextModule,
    Select,
    InputNumber,
    ConfirmDialog
],
    changeDetection: ChangeDetectionStrategy.Default,
    providers: [ConfirmationService]
})
export class TaskboardComponent implements OnInit {
  openTasks: Task[] = [];
  submittedTasks: Task[] = [];
  completedTasks: Task[] = [];
  rejectedTasks: Task[] = [];
  private allOpenTasks: Task[] = [];
  private allSubmittedTasks: Task[] = [];
  private allCompletedTasks: Task[] = [];
  private allRejectedTasks: Task[] = [];
  searchQuery = '';
  highlightTaskId = '';
  currentUserRole = '';
  currentUserId = '';
  rewardPoints = 0;
  activeFilter: TaskFilterId = 'available';
  private filterInitialized = false;

  readonly filters: { id: TaskFilterId; label: string }[] = [
    { id: 'available', label: 'Available' },
    { id: 'review', label: 'Awaiting review' },
    { id: 'approved', label: 'Approved' },
    { id: 'revision', label: 'Needs revision' }
  ];

  isLoading = false;
  isSubmitting = false;
  isVerifying = false;

  submissionDialogVisible = false;
  selectedTask: Task | null = null;
  taskSubmission: TaskSubmission = {
    submissionDetails: '',
    evidence: '',
    taskId: '',
    studentId: '',
    rubricChecked: []
  };
  rubricChecks: Record<string, boolean> = {};
  taskRubricText = '';
  templateRubricText = '';

  verifyDialogVisible = false;
  verifyApproved = true;
  verifyFeedback = '';
  verifyTarget: Task | null = null;

  taskDialogVisible = false;
  editMode = false;
  showAdvancedTaskOptions = false;
  taskToEdit: Task | null = null;
  selectedTemplateId: string | null = null;
  templateOptions: { label: string; value: string }[] = [];
  templates: TaskTemplate[] = [];

  templatesDialogVisible = false;
  templateEditMode = false;
  templateToEdit: TaskTemplate | null = null;
  templateFormVisible = false;

  taskTypeOptions = [
    { label: 'One student', value: 'SINGLE' },
    { label: 'Open to many', value: 'MULTIPLE' }
  ];

  constructor(
    private taskService: TaskService,
    private userService: UserService,
    private walletService: WalletService,
    private toastService: ToastService,
    private confirmationService: ConfirmationService,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      this.highlightTaskId = params.get('taskId') || '';
    });
    this.userService.getUserProfile().subscribe({
      next: (response) => {
        if (response.success && response.data) {
          this.applyUser(response.data.role, response.data.eliteId || '');
          this.loadTasks();
        } else {
          this.toastService.showError('Error loading user profile');
        }
      },
      error: () => this.toastService.showError('Error loading user profile')
    });
  }

  private applyUser(role: string, userId: string): void {
    this.currentUserRole = (role || '').toUpperCase();
    this.currentUserId = userId;
    if (!this.filterInitialized) {
      this.activeFilter = this.canVerifyTasks() ? 'review' : 'available';
      this.filterInitialized = true;
    }
    if (this.currentUserRole === 'STUDENT' && this.currentUserId) {
      this.loadRewardPoints(this.currentUserId);
    }
    if (this.canCreateTasks()) {
      this.loadTemplates();
    }
  }

  setFilter(id: TaskFilterId): void {
    this.activeFilter = id;
  }

  countFor(id: TaskFilterId): number {
    switch (id) {
      case 'available': return this.openTasks.length;
      case 'review': return this.submittedTasks.length;
      case 'approved': return this.completedTasks.length;
      case 'revision': return this.rejectedTasks.length;
    }
  }

  get visibleTasks(): Task[] {
    switch (this.activeFilter) {
      case 'available': return this.openTasks;
      case 'review': return this.submittedTasks;
      case 'approved': return this.completedTasks;
      case 'revision': return this.rejectedTasks;
    }
  }

  trackTask(_index: number, task: Task): string {
    return task.submissionId || task.id;
  }

  rowStatus(task: Task): TaskFilterId {
    const status = (task.submissionStatus || task.status || '').toUpperCase();
    if (status === 'SUBMITTED') return 'review';
    if (status === 'COMPLETED') return 'approved';
    if (status === 'REJECTED') return 'revision';
    return 'available';
  }

  statusLabel(task: Task): string {
    switch (this.rowStatus(task)) {
      case 'available': return 'Available';
      case 'review': return 'Awaiting review';
      case 'approved': return 'Approved';
      case 'revision': return 'Needs revision';
    }
  }

  emptyIcon(): string {
    switch (this.activeFilter) {
      case 'available': return 'pi-check-square';
      case 'review': return 'pi-inbox';
      case 'approved': return 'pi-verified';
      case 'revision': return 'pi-refresh';
    }
  }

  emptyMessage(): string {
    if (this.currentUserRole === 'STUDENT') {
      switch (this.activeFilter) {
        case 'available':
          return 'No open opportunities right now. Check back soon, or ask faculty what’s coming up.';
        case 'review':
          return 'Nothing waiting on faculty yet. Mark a task complete when you finish the work.';
        case 'approved':
          return 'No approved completions yet. Verified work appears here with Elite Points earned.';
        case 'revision':
          return 'No revisions needed. If faculty asks for changes, resubmit from here.';
      }
    }
    switch (this.activeFilter) {
      case 'available':
        return 'No open tasks yet. Add contribution work students can choose.';
      case 'review':
        return 'No submissions waiting for verification.';
      case 'approved':
        return 'No approved completions in this list yet.';
      case 'revision':
        return 'No submissions currently need revision.';
    }
  }

  loadTemplates(): void {
    this.taskService.getTaskTemplates().subscribe({
      next: (templates) => {
        this.templates = templates;
        this.templateOptions = templates.map(t => ({ label: t.title, value: t.id }));
      },
      error: () => {}
    });
  }

  loadRewardPoints(userId: string): void {
    this.walletService.getWalletBalance(userId).subscribe({
      next: (points) => this.rewardPoints = points,
      error: () => {}
    });
  }

  loadTasks(): void {
    this.isLoading = true;

    if (this.currentUserRole === 'STUDENT' && this.currentUserId) {
      forkJoin({
        open: this.taskService.getOpenTasks().pipe(catchError(() => of([]))),
        submissions: this.taskService.getSubmissionsByStudent(this.currentUserId).pipe(catchError(() => of([])))
      }).subscribe({
        next: ({ open, submissions }) => {
          const blockedIds = new Set(
            submissions
              .filter(s => s.status === 'SUBMITTED' || s.status === 'COMPLETED' || s.status === 'REJECTED')
              .map(s => s.taskId)
          );
          this.allOpenTasks = open.filter(t => !blockedIds.has(t.id));
          this.allSubmittedTasks = this.mapSubmissionsToTasks(
            submissions.filter(s => s.status === 'SUBMITTED')
          );
          this.allCompletedTasks = this.mapSubmissionsToTasks(
            submissions.filter(s => s.status === 'COMPLETED')
          );
          this.allRejectedTasks = this.mapSubmissionsToTasks(
            submissions.filter(s => s.status === 'REJECTED')
          );
          this.applySearchFilter();
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
          this.toastService.showError('Error loading tasks');
        }
      });
      return;
    }

    forkJoin({
      tasks: this.taskService.getTasks().pipe(catchError(() => of([]))),
      submitted: this.taskService.getSubmissionsByStatus('SUBMITTED').pipe(catchError(() => of([]))),
      completed: this.taskService.getSubmissionsByStatus('COMPLETED').pipe(catchError(() => of([]))),
      rejected: this.taskService.getSubmissionsByStatus('REJECTED').pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ tasks, submitted, completed, rejected }) => {
        this.allOpenTasks = tasks.filter(t => t.status === 'OPEN');
        this.allSubmittedTasks = this.mapSubmissionsToTasks(submitted);
        this.allCompletedTasks = this.mapSubmissionsToTasks(completed);
        this.allRejectedTasks = this.mapSubmissionsToTasks(rejected);
        this.applySearchFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.toastService.showError('Error loading tasks');
      }
    });
  }

  private mapSubmissionsToTasks(submissions: TaskSubmission[]): Task[] {
    return submissions.map(s => ({
      id: s.taskId,
      title: s.taskTitle || 'Task',
      description: s.taskDescription || s.submissionDetails || '',
      taskType: (s.taskType as Task['taskType']) || 'SINGLE',
      minLevel: 1,
      rewardPoints: s.rewardPoints || 0,
      createdBy: s.taskCreatedBy || '',
      status: (s.status || 'SUBMITTED') as Task['status'],
      createdAt: s.submittedAt || '',
      completedAt: s.verifiedAt,
      submissionId: s.id,
      submissionStatus: s.status,
      feedbackNotes: s.feedbackNotes,
      submissionDetails: s.submissionDetails,
      evidence: s.evidence,
      rubricChecked: s.rubricChecked || [],
      evidenceRequired: s.evidenceRequired,
      minNotesLength: s.minNotesLength,
      rubricChecklist: s.rubricChecklist || []
    }));
  }

  applySearchFilter(): void {
    const query = this.searchQuery.toLowerCase().trim();
    const matches = (t: Task) =>
      !query ||
      t.title.toLowerCase().includes(query) ||
      (t.description || '').toLowerCase().includes(query);

    this.openTasks = this.allOpenTasks.filter(matches);
    this.submittedTasks = this.allSubmittedTasks.filter(matches);
    this.completedTasks = this.allCompletedTasks.filter(matches);
    this.rejectedTasks = this.allRejectedTasks.filter(matches);
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.applySearchFilter();
  }

  canCreateTasks(): boolean {
    return ['FACULTY', 'MANAGEMENT', 'ADMIN'].includes(this.currentUserRole);
  }

  canVerifyTasks(): boolean {
    return ['FACULTY', 'MANAGEMENT', 'ADMIN'].includes(this.currentUserRole);
  }

  canManageTask(_task: Task): boolean {
    return ['ADMIN', 'MANAGEMENT', 'FACULTY'].includes(this.currentUserRole);
  }

  openSubmitDialog(task: Task): void {
    if (!this.currentUserId) {
      this.toastService.showError('User information not available');
      return;
    }

    this.selectedTask = task;
    this.taskSubmission = {
      submissionDetails: '',
      evidence: '',
      taskId: task.id,
      studentId: this.currentUserId,
      rubricChecked: []
    };
    this.rubricChecks = {};
    (task.rubricChecklist || []).forEach(item => {
      this.rubricChecks[item] = false;
    });
    this.submissionDialogVisible = true;
  }

  submitTask(): void {
    if (!this.selectedTask || !this.taskSubmission) {
      return;
    }

    const minNotes = this.selectedTask.minNotesLength ?? 40;
    const notes = (this.taskSubmission.submissionDetails || '').trim();
    if (notes.length < minNotes) {
      this.toastService.showError(`Submission notes must be at least ${minNotes} characters`);
      return;
    }
    if (this.selectedTask.evidenceRequired !== false && !(this.taskSubmission.evidence || '').trim()) {
      this.toastService.showError('Evidence is required for this task');
      return;
    }
    const rubric = this.selectedTask.rubricChecklist || [];
    if (rubric.length && rubric.some(item => !this.rubricChecks[item])) {
      this.toastService.showError('Confirm all verification checklist items');
      return;
    }
    this.taskSubmission.rubricChecked = rubric.filter(item => this.rubricChecks[item]);

    this.isSubmitting = true;
    this.taskService.submitTask(this.taskSubmission).subscribe({
      next: () => {
        this.toastService.showSuccess('Sent for faculty review');
        this.activeFilter = 'review';
        this.submissionDialogVisible = false;
        this.loadTasks();
        this.isSubmitting = false;
      },
      error: (err) => {
        this.isSubmitting = false;
        this.toastService.showError(
          err.error?.friendlyMessage || err.error?.message || 'Submission failed'
        );
      }
    });
  }

  openVerifyDialog(task: Task, approved: boolean): void {
    if (!task.submissionId) {
      this.toastService.showError('Submission ID not found');
      return;
    }
    this.verifyTarget = task;
    this.verifyApproved = approved;
    this.verifyFeedback = approved ? 'Great work — approved.' : '';
    this.verifyDialogVisible = true;
  }

  confirmVerify(): void {
    if (!this.verifyTarget?.submissionId) {
      return;
    }
    if (!this.verifyApproved && !this.verifyFeedback.trim()) {
      this.toastService.showError('Please provide feedback when rejecting');
      return;
    }

    this.isVerifying = true;
    this.taskService.verifyTask(
      this.verifyTarget.submissionId,
      this.verifyApproved,
      this.verifyFeedback.trim()
    ).subscribe({
      next: () => {
        this.toastService.showSuccess(this.verifyApproved ? 'Submission approved' : 'Submission rejected');
        this.verifyDialogVisible = false;
        this.loadTasks();
        if (this.currentUserRole === 'STUDENT' && this.currentUserId) {
          this.loadRewardPoints(this.currentUserId);
        }
        this.isVerifying = false;
      },
      error: () => {
        this.isVerifying = false;
      }
    });
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    return new Date(dateString).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }

  editTask(task: Task): void {
    this.editMode = true;
    this.selectedTemplateId = null;
    this.showAdvancedTaskOptions = !!(task.minNotesLength || task.rubricChecklist?.length);
    this.taskToEdit = {
      ...task,
      evidenceRequired: task.evidenceRequired !== false,
      minNotesLength: task.minNotesLength ?? 40,
      rubricChecklist: [...(task.rubricChecklist || [])]
    };
    this.taskRubricText = (task.rubricChecklist || []).join('\n');
    this.taskDialogVisible = true;
  }

  addNewTask(): void {
    this.editMode = false;
    this.selectedTemplateId = null;
    this.showAdvancedTaskOptions = false;
    this.taskToEdit = {
      id: '',
      title: '',
      description: '',
      taskType: 'SINGLE',
      minLevel: 1,
      rewardPoints: 10,
      createdBy: this.currentUserId,
      status: 'OPEN',
      createdAt: new Date().toISOString(),
      evidenceRequired: true,
      minNotesLength: 40,
      rubricChecklist: []
    };
    this.taskRubricText = '';
    this.loadTemplates();
    this.taskDialogVisible = true;
  }

  onTemplateSelected(): void {
    if (!this.selectedTemplateId || !this.taskToEdit) {
      return;
    }
    const template = this.templates.find(t => t.id === this.selectedTemplateId);
    if (!template) {
      return;
    }
    this.taskToEdit.title = template.title;
    this.taskToEdit.description = template.description;
    this.taskToEdit.taskType = template.taskType;
    this.taskToEdit.minLevel = template.minLevel;
    this.taskToEdit.rewardPoints = template.rewardPoints;
    this.taskToEdit.evidenceRequired = template.evidenceRequired !== false;
    this.taskToEdit.minNotesLength = template.minNotesLength ?? 40;
    this.taskToEdit.rubricChecklist = [...(template.rubricChecklist || [])];
    this.taskRubricText = (template.rubricChecklist || []).join('\n');
  }

  saveTask(): void {
    if (!this.taskToEdit) return;

    this.taskToEdit.evidenceRequired = this.taskToEdit.evidenceRequired !== false;
    this.taskToEdit.minNotesLength = this.taskToEdit.minNotesLength ?? 40;
    this.taskToEdit.rubricChecklist = parseLines(this.taskRubricText);

    if (this.editMode) {
      this.taskService.updateTask(this.taskToEdit.id, this.taskToEdit).subscribe({
        next: () => {
          this.toastService.showSuccess('Task updated successfully');
          this.taskDialogVisible = false;
          this.loadTasks();
        },
        error: () => {}
      });
      return;
    }

    if (this.selectedTemplateId) {
      const template = this.templates.find(t => t.id === this.selectedTemplateId);
      const unchanged = template
        && this.taskToEdit.title === template.title
        && this.taskToEdit.description === template.description
        && this.taskToEdit.taskType === template.taskType
        && this.taskToEdit.minLevel === template.minLevel
        && this.taskToEdit.rewardPoints === template.rewardPoints
        && (this.taskToEdit.evidenceRequired !== false) === (template.evidenceRequired !== false)
        && (this.taskToEdit.minNotesLength ?? 40) === (template.minNotesLength ?? 40)
        && JSON.stringify(this.taskToEdit.rubricChecklist || []) === JSON.stringify(template.rubricChecklist || []);

      if (unchanged) {
        this.taskService.createTaskFromTemplate(this.selectedTemplateId).subscribe({
          next: () => {
            this.toastService.showSuccess('Task created from template successfully');
            this.taskDialogVisible = false;
            this.loadTasks();
          },
          error: () => {}
        });
        return;
      }
    }

    this.taskService.createTask(this.taskToEdit).subscribe({
      next: () => {
        this.toastService.showSuccess('Task created successfully');
        this.taskDialogVisible = false;
        this.loadTasks();
      },
      error: () => {}
    });
  }

  openTemplatesDialog(): void {
    this.loadTemplates();
    this.templateFormVisible = false;
    this.templateToEdit = null;
    this.templatesDialogVisible = true;
  }

  addNewTemplate(): void {
    this.templateEditMode = false;
    this.templateToEdit = {
      id: '',
      title: '',
      description: '',
      taskType: 'SINGLE',
      minLevel: 1,
      rewardPoints: 10,
      createdBy: this.currentUserId,
      createdAt: new Date().toISOString(),
      evidenceRequired: true,
      minNotesLength: 40,
      rubricChecklist: []
    };
    this.templateRubricText = '';
    this.templateFormVisible = true;
  }

  editTemplate(template: TaskTemplate): void {
    this.templateEditMode = true;
    this.templateToEdit = {
      ...template,
      evidenceRequired: template.evidenceRequired !== false,
      minNotesLength: template.minNotesLength ?? 40,
      rubricChecklist: [...(template.rubricChecklist || [])]
    };
    this.templateRubricText = (template.rubricChecklist || []).join('\n');
    this.templateFormVisible = true;
  }

  cancelTemplateForm(): void {
    this.templateFormVisible = false;
    this.templateToEdit = null;
  }

  saveTemplate(): void {
    if (!this.templateToEdit) return;

    this.templateToEdit.evidenceRequired = this.templateToEdit.evidenceRequired !== false;
    this.templateToEdit.minNotesLength = this.templateToEdit.minNotesLength ?? 40;
    this.templateToEdit.rubricChecklist = parseLines(this.templateRubricText);

    if (this.templateEditMode) {
      this.taskService.updateTaskTemplate(this.templateToEdit.id, this.templateToEdit).subscribe({
        next: () => {
          this.toastService.showSuccess('Template updated successfully');
          this.templateFormVisible = false;
          this.loadTemplates();
        },
        error: () => {}
      });
      return;
    }

    this.taskService.createTaskTemplate(this.templateToEdit).subscribe({
      next: () => {
        this.toastService.showSuccess('Template created successfully');
        this.templateFormVisible = false;
        this.loadTemplates();
      },
      error: () => {}
    });
  }

  deleteTemplate(template: TaskTemplate): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete the template "${template.title}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Yes',
      rejectLabel: 'No',
      accept: () => {
        this.taskService.deleteTaskTemplate(template.id).subscribe({
          next: () => {
            this.toastService.showSuccess('Template deleted successfully');
            this.loadTemplates();
          },
          error: () => {}
        });
      }
    });
  }

  deleteTask(task: Task): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete the task "${task.title}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Yes',
      rejectLabel: 'No',
      accept: () => {
        this.taskService.deleteTask(task.id).subscribe({
          next: () => {
            this.toastService.showSuccess('Task deleted successfully');
            this.loadTasks();
          },
          error: () => {}
        });
      }
    });
  }
}
