import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

import { UserService } from '../../core/services/user.service';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { FacultyDashboardComponent } from './faculty-dashboard/faculty-dashboard.component';
import { StudentDashboardComponent } from './student-dashboard/student-dashboard.component';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [AdminDashboardComponent, FacultyDashboardComponent, StudentDashboardComponent]
})
export class DashboardComponent implements OnInit {
  role = '';
  loaded = false;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUserProfile().subscribe({
      next: (response) => {
        this.role = (response.data?.role || this.userService.getCurrentUser()?.role || '').toUpperCase();
        this.loaded = true;
      },
      error: () => {
        this.role = (this.userService.getCurrentUser()?.role || '').toUpperCase();
        this.loaded = true;
      }
    });
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
