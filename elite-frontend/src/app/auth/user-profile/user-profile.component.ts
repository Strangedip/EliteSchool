import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA, AfterViewInit, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TabViewModule } from 'primeng/tabview';
import { TableModule } from 'primeng/table';

import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { RewardService } from '../../core/services/reward.service';
import { RewardTransaction } from '../../core/models/reward.model';
import { TaskService } from '../../core/services/task.service';
import { TaskSubmission } from '../../core/models/task.model';

interface UserTaskDisplay {
  id: string;
  title: string;
  status: string;
  submittedAt?: string;
  rewardPoints: number;
  feedbackNotes?: string;
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, TabViewModule, TableModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UserProfileComponent implements OnInit, AfterViewInit {
  user: User | null = null;
  rewardPoints: number = 0;
  transactions: RewardTransaction[] = [];
  userTasks: UserTaskDisplay[] = [];

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private rewardService: RewardService,
    private taskService: TaskService,
    private el: ElementRef
  ) { }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  ngAfterViewInit(): void {
    // Apply styling to the TabView after view is initialized
    setTimeout(() => {
      this.stylingTabView();
    }, 100);
  }

  loadUserProfile(): void {
    // Using User Service to get current user
    this.user = this.userService.getCurrentUser();
    
    if (this.user && this.user.role === 'STUDENT') {
      const userId = this.user.id || this.user.eliteId;
      if (userId) {
        this.loadRewardPoints(userId);
        this.loadTransactions(userId);
        this.loadUserTasks(userId);
      }
    }
  }

  loadRewardPoints(userId: string): void {
    this.rewardService.getWalletBalance(userId).subscribe(points => {
      this.rewardPoints = points;
    });
  }

  loadTransactions(userId: string): void {
    this.rewardService.getTransactionHistory(userId).subscribe(transactions => {
      this.transactions = transactions;
    });
  }

  loadUserTasks(userId: string): void {
    // Get all task submissions for the student
    this.taskService.getSubmissionsByStudent(userId).subscribe(submissions => {
      const taskPromises: Promise<UserTaskDisplay>[] = submissions.map(submission => {
        // For each submission, get the related task to get its title and points
        return new Promise<UserTaskDisplay>((resolve) => {
          this.taskService.getTaskById(submission.taskId).subscribe(task => {
            resolve({
              id: submission.id || '',
              title: task.title,
              status: submission.status || 'UNKNOWN',
              submittedAt: submission.submittedAt,
              rewardPoints: task.rewardPoints,
              feedbackNotes: submission.feedbackNotes
            });
          }, error => {
            // Fallback if task fetch fails
            resolve({
              id: submission.id || '',
              title: 'Unknown Task',
              status: submission.status || 'UNKNOWN',
              submittedAt: submission.submittedAt,
              rewardPoints: 0,
              feedbackNotes: submission.feedbackNotes
            });
          });
        });
      });

      // Wait for all task lookups to complete
      Promise.all(taskPromises).then(taskDisplays => {
        this.userTasks = taskDisplays;
      });
    });
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Apply custom styling to the TabView active bar
   */
  stylingTabView(): void {
    // Find all active bars in the component
    const activeBarElements = this.el.nativeElement.querySelectorAll('.p-tablist-active-bar, .p-tabview-ink-bar');
    
    // Apply styling to each active bar
    activeBarElements.forEach((element: Element) => {
      if (element instanceof HTMLElement) {
        element.style.background = 'linear-gradient(90deg, #0077b6, #00d4ff)';
        element.style.height = '3px';
        element.style.borderRadius = '3px';
        element.style.boxShadow = '0 1px 3px rgba(0, 180, 216, 0.3)';
        element.style.zIndex = '2';
      }
    });
    
    // Also style all text in tab headers
    const tabHeaders = this.el.nativeElement.querySelectorAll('.p-tabview-nav li, .p-tablist-item');
    
    tabHeaders.forEach((header: Element) => {
      if (header instanceof HTMLElement) {
        // Remove any background color
        header.style.backgroundColor = 'transparent';
        
        // Set default text color
        const titleElements = header.querySelectorAll('.p-tabview-title, .p-tablist-item-title, span');
        titleElements.forEach((titleEl: Element) => {
          if (titleEl instanceof HTMLElement) {
            titleEl.style.color = '#a3c1d9';
          }
        });
        
        // If this is the active tab, set text color
        if (header.classList.contains('p-highlight')) {
          const activeTitleElements = header.querySelectorAll('.p-tabview-title, .p-tablist-item-title, span');
          activeTitleElements.forEach((titleEl: Element) => {
            if (titleEl instanceof HTMLElement) {
              titleEl.style.color = '#00d4ff';
            }
          });
        }
      }
    });
  }
}
