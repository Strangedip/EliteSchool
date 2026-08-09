import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { FacultyDashboardComponent } from './faculty-dashboard/faculty-dashboard.component';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  imports: [AdminDashboardComponent, FacultyDashboardComponent, StudentDashboardComponent]
})
export class DashboardComponent implements OnInit {
  role = '';
  loaded = false;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    // AuthGuard already resolved the profile — render immediately from cache.
    this.applyRole(this.userService.getCurrentUser()?.role);
    if (this.loaded) {
      return;
    }

    this.userService.getUserProfile().subscribe({
      next: (response) => this.applyRole(response.data?.role || this.userService.getCurrentUser()?.role),
      error: () => this.applyRole(this.userService.getCurrentUser()?.role)
    });
  }

  private applyRole(role: string | undefined | null): void {
    this.role = (role || '').toUpperCase();
    this.loaded = !!this.role;
  }

  get isAdmin(): boolean {
    return this.role === 'ADMIN' || this.role === 'MANAGEMENT';
  }

  get isFaculty(): boolean {
    return this.role === 'FACULTY';
  }

  get isStudent(): boolean {
    return this.role === 'STUDENT';
  }
}
