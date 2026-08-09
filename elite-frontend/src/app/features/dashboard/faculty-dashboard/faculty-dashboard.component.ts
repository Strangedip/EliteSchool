import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { TaskService } from '../../../core/services/task.service';
import { CourseService } from '../../../core/services/course.service';
import { UserService } from '../../../core/services/user.service';
import { TaskSubmission, Task } from '../../../core/models/task.model';

@Component({
  selector: 'app-faculty-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './faculty-dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.Default,
  styleUrls: ['../dashboard-shared.scss']
})
export class FacultyDashboardComponent implements OnInit {
  loading = true;
  currentUserName = '';

  pendingCount = 0;
  pendingSubmissions: TaskSubmission[] = [];
  myOpenTasks: Task[] = [];
  myOpenTaskCount = 0;
  activeCourseCount = 0;

  constructor(
    private taskService: TaskService,
    private courseService: CourseService,
    private userService: UserService
  ) {}

  ngOnInit(): void {
    this.currentUserName = this.userService.getCurrentUser()?.name || 'Faculty';
    this.loadData();
  }

  private loadData(): void {
    const currentUserId = this.userService.getCurrentUser()?.eliteId || '';

    forkJoin({
      pending: this.taskService.getSubmissionsByStatus('SUBMITTED').pipe(catchError(() => of([]))),
      tasks: this.taskService.getTasks().pipe(catchError(() => of([]))),
      courses: this.courseService.getCourses().pipe(catchError(() => of([])))
    }).subscribe({
      next: ({ pending, tasks, courses }) => {
        this.pendingCount = pending.length;
        this.pendingSubmissions = pending.slice(0, 6);
        const mine = tasks.filter(t => t.status === 'OPEN' && (!currentUserId || t.createdBy === currentUserId));
        this.myOpenTaskCount = mine.length;
        this.myOpenTasks = mine.slice(0, 6);
        this.activeCourseCount = courses.filter(c => c.active).length;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
