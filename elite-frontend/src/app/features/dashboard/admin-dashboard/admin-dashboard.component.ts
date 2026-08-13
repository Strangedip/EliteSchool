import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { UserService } from '../../../core/services/user.service';
import { CourseService } from '../../../core/services/course.service';
import { PointsService, PointsBalanceEntry } from '../../../core/services/points.service';
import { User } from '../../../core/models/user.model';

interface RoleCount {
  role: string;
  count: number;
  icon: string;
}

@Component({
    selector: 'app-admin-dashboard',
    imports: [RouterLink],
    templateUrl: './admin-dashboard.component.html',
    changeDetection: ChangeDetectionStrategy.Default,
    styleUrls: ['../dashboard-shared.scss']
})
export class AdminDashboardComponent implements OnInit {
  loading = true;
  currentUserName = '';
  roleLabel = 'Admin';

  totalUsers = 0;
  activeCourseCount = 0;
  totalCourseCount = 0;
  totalPointsInCirculation = 0;
  topEarner: PointsBalanceEntry | null = null;
  topEarnerName = '';

  roleCounts: RoleCount[] = [];
  recentUsers: User[] = [];

  private roleIcons: Record<string, string> = {
    ADMIN: 'pi pi-shield',
    MANAGEMENT: 'pi pi-briefcase',
    FACULTY: 'pi pi-graduation-cap',
    STUDENT: 'pi pi-user',
    GUEST: 'pi pi-user-plus'
  };

  constructor(
    private userService: UserService,
    private courseService: CourseService,
    private pointsService: PointsService
  ) {}

  ngOnInit(): void {
    const user = this.userService.getCurrentUser();
    this.currentUserName = user?.name || 'Admin';
    this.roleLabel = (user?.role || 'ADMIN').toUpperCase() === 'MANAGEMENT' ? 'Management' : 'Admin';
    this.loadData();
  }

  private loadData(): void {
    forkJoin({
      users: this.userService.getAllUsers().pipe(
        catchError(() => of({ success: false, data: [] as User[], message: '' }))
      ),
      courses: this.courseService.getCourses().pipe(catchError(() => of([]))),
      leaderboard: this.pointsService.getLeaderboard(100).pipe(
        catchError(() => of([] as PointsBalanceEntry[]))
      )
    }).subscribe({
      next: ({ users, courses, leaderboard }) => {
        const allUsers = users.data ?? [];
        this.totalUsers = allUsers.length;
        this.roleCounts = this.groupByRole(allUsers);
        this.recentUsers = [...allUsers]
          .sort((a, b) => (b.createdAt || '').localeCompare(a.createdAt || ''))
          .slice(0, 5);

        this.totalCourseCount = courses.length;
        this.activeCourseCount = courses.filter(c => c.active).length;

        this.totalPointsInCirculation = leaderboard.reduce((sum, w) => sum + (w.balance || 0), 0);
        this.topEarner = leaderboard[0] || null;
        if (this.topEarner) {
          const match = allUsers.find(u => u.eliteId === this.topEarner!.studentId);
          this.topEarnerName = this.topEarner.studentName || match?.name || 'Unknown';
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading admin dashboard data', error);
        this.loading = false;
      }
    });
  }

  private groupByRole(users: User[]): RoleCount[] {
    const counts = new Map<string, number>();
    for (const u of users) {
      const role = (u.role || 'UNKNOWN').toUpperCase();
      counts.set(role, (counts.get(role) || 0) + 1);
    }
    return Array.from(counts.entries())
      .map(([role, count]) => ({ role, count, icon: this.roleIcons[role] || 'pi pi-user' }))
      .sort((a, b) => b.count - a.count);
  }
}
