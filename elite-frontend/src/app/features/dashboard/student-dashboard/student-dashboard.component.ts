import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { WalletService } from '../../../core/services/wallet.service';
import { TaskService } from '../../../core/services/task.service';
import { CourseService } from '../../../core/services/course.service';
import { UserService } from '../../../core/services/user.service';
import { Transaction } from '../../../core/models/wallet.model';
import { Task } from '../../../core/models/task.model';

@Component({
  selector: 'app-student-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './student-dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.Default,
  styleUrls: ['../dashboard-shared.scss']
})
export class StudentDashboardComponent implements OnInit {
  loading = true;
  currentUserName = '';
  currentUserId = '';
  roleLabel = 'Student';

  walletBalance = 0;
  recentTransactions: Transaction[] = [];
  leaderboardRank: number | null = null;
  leaderboardTotal = 0;
  openTaskCount = 0;
  openTasks: Task[] = [];
  activeCourseCount = 0;

  constructor(
    private walletService: WalletService,
    private taskService: TaskService,
    private courseService: CourseService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    const user = this.userService.getCurrentUser();
    this.currentUserName = user?.name || 'Student';
    this.currentUserId = user?.eliteId || '';
    this.roleLabel = 'Student';

    if (this.currentUserId) {
      this.loadData();
      return;
    }

    this.userService.getUserProfile().subscribe({
      next: (response) => {
        const resolved = response.data || this.userService.getCurrentUser();
        this.currentUserName = resolved?.name || 'Student';
        this.currentUserId = resolved?.eliteId || '';
        this.loadData();
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private loadData(): void {
    if (!this.currentUserId) {
      this.loading = false;
      return;
    }

    forkJoin({
      balance: this.walletService.getWalletBalance(this.currentUserId).pipe(catchError(() => of(0))),
      transactions: this.walletService.getTransactionHistory(this.currentUserId).pipe(catchError(() => of([]))),
      leaderboard: this.walletService.getLeaderboard(100).pipe(catchError(() => of([]))),
      tasks: this.taskService.getOpenTasks().pipe(catchError(() => of([]))),
      submissions: this.taskService.getSubmissionsByStudent(this.currentUserId).pipe(catchError(() => of([]))),
      courses: this.courseService.getCourses().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ balance, transactions, leaderboard, tasks, submissions, courses }) => {
        this.walletBalance = balance;
        this.recentTransactions = transactions.slice(0, 5);
        this.leaderboardTotal = leaderboard.length;
        const rankIndex = leaderboard.findIndex(w => w.studentId === this.currentUserId);
        this.leaderboardRank = rankIndex === -1 ? null : rankIndex + 1;

        const taken = new Set(
          submissions
            .filter(s => s.status === 'SUBMITTED' || s.status === 'COMPLETED' || s.status === 'REJECTED')
            .map(s => s.taskId)
        );
        const available = tasks.filter(t => !taken.has(t.id));
        this.openTaskCount = available.length;
        this.openTasks = available.slice(0, 6);
        this.activeCourseCount = courses.filter(c => c.active).length;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
