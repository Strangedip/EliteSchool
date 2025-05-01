import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Task } from '../../core/models/task.model';
import { TaskService } from '../../core/services/task.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { RewardService } from '../../core/services/reward.service';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextarea } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';

@Component({
  selector: 'app-taskboard',
  templateUrl: './taskboard.component.html',
  styleUrls: ['./taskboard.component.scss'],
  standalone: true,
  imports: [CommonModule, ButtonModule, FormsModule, DialogModule, InputTextarea, InputTextModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TaskboardComponent implements OnInit {
  openTasks: Task[] = [];
  submittedTasks: Task[] = [];
  completedTasks: Task[] = [];
  allTasks: Task[] = [];
  searchQuery: string = '';
  currentUserRole: string = '';
  rewardPoints: number = 0;
  
  // Loading states
  isLoading: boolean = false;
  isSubmitting: boolean = false;
  isVerifying: boolean = false;
  
  // Task submission properties
  submissionDialogVisible: boolean = false;
  selectedTask: Task | null = null;
  taskSubmission: any = {
    submissionDetails: '',
    evidence: ''
  };

  constructor(
    private taskService: TaskService, 
    private authService: AuthService,
    private userService: UserService,
    private rewardService: RewardService
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadTasks();
  }

  loadUserInfo(): void {
    const user = this.userService.getCurrentUser();
    if (user) {
      this.currentUserRole = user.role;
      if (user.role === 'STUDENT') {
        const userId = user.id || user.eliteId;
        if (userId) {
          this.loadRewardPoints(userId);
        }
      }
    }
  }

  loadRewardPoints(userId: string): void {
    this.rewardService.getWalletBalance(userId).subscribe((points: number) => {
      this.rewardPoints = points;
    });
  }

  loadTasks(): void {
    this.isLoading = true;
    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.allTasks = tasks;
        this.filterTasks();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        this.isLoading = false;
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

  addTask(): void {
    // Navigate to task creation component
    console.log('Adding new task...');
  }

  startTask(task: Task): void {
    this.selectedTask = task;
    const user = this.userService.getCurrentUser();
    const userId = user ? (user.id || user.eliteId) : '';
    
    if (!userId) {
      console.error('User ID not found');
      return;
    }
    
    this.taskSubmission = {
      submissionDetails: '',
      evidence: '',
      taskId: task.id,
      studentId: userId
    };
    this.submissionDialogVisible = true;
  }

  submitTask(): void {
    if (this.selectedTask && this.taskSubmission) {
      this.isSubmitting = true;
      this.taskService.submitTask(this.taskSubmission).subscribe({
        next: (response) => {
          console.log('Task submitted successfully', response);
          this.submissionDialogVisible = false;
          this.loadTasks(); // Reload tasks to update the board
          this.isSubmitting = false;
        },
        error: (error) => {
          console.error('Error submitting task', error);
          this.isSubmitting = false;
        }
      });
    }
  }

  verifyTask(task: Task, approved: boolean): void {
    this.isVerifying = true;
    const user = this.userService.getCurrentUser();
    const userId = user ? (user.id || user.eliteId) : '';
    
    if (!userId) {
      console.error('User ID not found');
      this.isVerifying = false;
      return;
    }
    
    const feedback = approved ? 'Approved' : 'Rejected';
    
    this.taskService.verifyTask(task.id, userId, approved, feedback)
      .subscribe({
        next: () => {
          console.log('Task verification complete');
          this.loadTasks(); // Reload tasks to update the board
          this.isVerifying = false;
        },
        error: (error) => {
          console.error('Error verifying task', error);
          this.isVerifying = false;
        }
      });
  }

  getPriorityClass(priority: string): string {
    switch (priority.toLowerCase()) {
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
}
