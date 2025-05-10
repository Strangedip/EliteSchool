import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task, TaskSubmission } from '../../core/models/task.model';
import { TaskService } from '../../core/services/task.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { WalletService } from '../../core/services/wallet.service';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextarea } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-taskboard',
  templateUrl: './taskboard.component.html',
  styleUrls: ['./taskboard.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    ButtonModule, 
    FormsModule, 
    DialogModule, 
    InputTextarea, 
    InputTextModule,
    DropdownModule,
    InputNumberModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TaskboardComponent implements OnInit {
  openTasks: Task[] = [];
  submittedTasks: Task[] = [];
  completedTasks: Task[] = [];
  allTasks: Task[] = [];
  searchQuery: string = '';
  currentUserRole: string = '';
  currentUserId: string = '';
  rewardPoints: number = 0;
  
  // Loading states
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isVerifying: boolean = false;
  
  // Task submission properties
  submissionDialogVisible: boolean = false;
  selectedTask: Task | null = null;
  taskSubmission: TaskSubmission = {
    submissionDetails: '',
    evidence: '',
    taskId: '',
    studentId: ''
  };

  // Task edit properties
  taskDialogVisible: boolean = false;
  editMode: boolean = false;
  taskToEdit: Task | null = null;
  
  // Priority options
  priorityOptions = [
    { label: 'High', value: 'high' },
    { label: 'Medium', value: 'medium' },
    { label: 'Low', value: 'low' }
  ];
  
  // Task type options
  taskTypeOptions = [
    { label: 'Single', value: 'SINGLE' },
    { label: 'Multiple', value: 'MULTIPLE' }
  ];

  constructor(
    private taskService: TaskService, 
    private authService: AuthService,
    private userService: UserService,
    private walletService: WalletService,
    private toastService: ToastService,
    private confirmationService: ConfirmationService
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadTasks();
  }

  loadUserInfo(): void {
    const user = this.userService.getCurrentUser();
    if (user) {
      this.currentUserRole = user.role;
      this.currentUserId = user.id || user.eliteId || '';
      
      if (user.role === 'STUDENT' && this.currentUserId) {
        this.loadRewardPoints(this.currentUserId);
      }
    } else {
      // If user info not available in memory, try to fetch it
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserRole = response.data.role;
            this.currentUserId = response.data.id || response.data.eliteId || '';
            
            if (response.data.role === 'STUDENT' && this.currentUserId) {
              this.loadRewardPoints(this.currentUserId);
            }
          }
        },
        error: (error) => {
          console.error('Error loading user profile:', error);
          this.toastService.showError('Error loading user profile');
        }
      });
    }
  }

  loadRewardPoints(userId: string): void {
    this.walletService.getWalletBalance(userId).subscribe({
      next: (points: number) => {
        this.rewardPoints = points;
      },
      error: (error: any) => {
        console.error('Error loading reward points:', error);
      }
    });
  }

  loadTasks(): void {
    this.isLoading = true;
    
    // For student users, we need to get submitted/completed tasks separately
    if (this.currentUserRole === 'STUDENT' && this.currentUserId) {
      // First get open tasks
      this.taskService.getOpenTasks().subscribe({
        next: (tasks) => {
          this.openTasks = tasks;
          this.isLoading = false;
          
          // Then get student's submissions
          this.loadStudentSubmissions();
        },
        error: (error) => {
          console.error('Error loading open tasks:', error);
          this.isLoading = false;
          this.toastService.showError('Error loading tasks');
        }
      });
    } else {
      // For faculty/admin, get all tasks
      this.taskService.getTasks().subscribe({
        next: (tasks) => {
          this.allTasks = tasks;
          this.filterTasks();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading tasks:', error);
          this.isLoading = false;
          this.toastService.showError('Error loading tasks');
        }
      });
    }
  }
  
  loadStudentSubmissions(): void {
    if (!this.currentUserId) return;
    
    this.taskService.getSubmissionsByStudent(this.currentUserId).subscribe({
      next: (submissions) => {
        // Process submissions to update task lists
        submissions.forEach(submission => {
          if (submission.task) {
            // Add submission info to the task
            submission.task.submissionId = submission.id;
            submission.task.submissionStatus = submission.status;
            
            if (submission.status === 'APPROVED') {
              this.completedTasks.push(submission.task);
            } else if (submission.status === 'PENDING') {
              this.submittedTasks.push(submission.task);
            }
          }
        });
      },
      error: (error) => {
        console.error('Error loading student submissions:', error);
      }
    });
  }

  filterTasks(): void {
    const filteredTasks = this.searchQuery 
      ? this.allTasks.filter(task => 
          task.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          task.description.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      : this.allTasks;
      
    this.openTasks = filteredTasks.filter(task => task.status === 'OPEN');
    this.submittedTasks = filteredTasks.filter(task => task.status === 'SUBMITTED');
    this.completedTasks = filteredTasks.filter(task => task.status === 'COMPLETED');
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterTasks();
  }

  canCreateTasks(): boolean {
    return ['FACULTY', 'MANAGEMENT', 'ADMIN'].includes(this.currentUserRole);
  }

  canVerifyTasks(): boolean {
    return ['FACULTY', 'MANAGEMENT', 'ADMIN'].includes(this.currentUserRole);
  }

  startTask(task: Task): void {
    this.selectedTask = task;
    
    if (!this.currentUserId) {
      this.toastService.showError('User information not available');
      return;
    }
    
    this.taskSubmission = {
      submissionDetails: '',
      evidence: '',
      taskId: task.id,
      studentId: this.currentUserId
    };
    this.submissionDialogVisible = true;
  }

  submitTask(): void {
    if (this.selectedTask && this.taskSubmission) {
      this.isSubmitting = true;
      this.taskService.submitTask(this.taskSubmission).subscribe({
        next: (response) => {
          this.toastService.showSuccess('Task submitted successfully');
          this.submissionDialogVisible = false;
          this.loadTasks(); // Reload tasks to update the board
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error submitting task', error);
          this.toastService.showError('Error submitting task');
          this.isSubmitting = false;
        }
      });
    }
  }

  verifyTask(taskId: string, submissionId: string | undefined, approved: boolean): void {
    if (!submissionId) {
      this.toastService.showError('Submission ID not found');
      return;
    }
    
    this.isVerifying = true;
    
    if (!this.currentUserId) {
      this.toastService.showError('User information not available');
      this.isVerifying = false;
      return;
    }
    
    const feedback = approved ? 'Approved' : 'Rejected';
    
    this.taskService.verifyTask(submissionId, this.currentUserId, approved, feedback)
      .subscribe({
        next: () => {
          this.toastService.showSuccess('Task verification complete');
          this.loadTasks(); // Reload tasks to update the board
          this.isVerifying = false;
        },
        error: (error) => {
          console.error('Error verifying task', error);
          this.toastService.showError('Error verifying task');
          this.isVerifying = false;
        }
      });
  }

  getPriorityClass(priority: string): string {
    switch (priority?.toLowerCase()) {
      case 'high':
        return 'priority-high';
      case 'medium':
        return 'priority-medium';
      case 'low':
        return 'priority-low';
      default:
        return 'priority-medium';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
  
  isAdmin(): boolean {
    return this.currentUserRole === 'ADMIN' || this.currentUserRole === 'MANAGEMENT';
  }
  
  editTask(task: Task): void {
    this.editMode = true;
    this.taskToEdit = {...task}; // Clone to avoid modifying the original task
    this.taskDialogVisible = true;
  }
  
  addNewTask(): void {
    this.editMode = false;
    const defaultTask: Task = {
      id: '',
      title: '',
      description: '',
      taskType: 'SINGLE',
      minLevel: 1,
      rewardPoints: 10,
      createdBy: this.currentUserId,
      status: 'OPEN',
      createdAt: new Date().toISOString(),
      priority: 'medium'
    };
    this.taskToEdit = defaultTask;
    this.taskDialogVisible = true;
  }
  
  saveTask(): void {
    if (!this.taskToEdit) return;
    
    if (this.editMode) {
      // Update existing task
      this.taskService.updateTask(this.taskToEdit.id, this.taskToEdit).subscribe({
        next: (updatedTask) => {
          this.toastService.showSuccess('Task updated successfully');
          this.taskDialogVisible = false;
          this.loadTasks(); // Reload tasks to update the board
        },
        error: (error) => {
          console.error('Error updating task', error);
          this.toastService.showError('Error updating task');
        }
      });
    } else {
      // Create new task
      this.taskService.createTask(this.taskToEdit).subscribe({
        next: (newTask) => {
          this.toastService.showSuccess('Task created successfully');
          this.taskDialogVisible = false;
          this.loadTasks(); // Reload tasks to update the board
        },
        error: (error) => {
          console.error('Error creating task', error);
          this.toastService.showError('Error creating task');
        }
      });
    }
  }
  
  deleteTask(task: Task): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete the task "${task.title}"?`,
      header: 'Confirm Delete',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.taskService.deleteTask(task.id).subscribe({
          next: () => {
            this.toastService.showSuccess('Task deleted successfully');
            this.loadTasks(); // Reload tasks to update the board
          },
          error: (error) => {
            console.error('Error deleting task', error);
            this.toastService.showError('Error deleting task');
          }
        });
      }
    });
  }
}
