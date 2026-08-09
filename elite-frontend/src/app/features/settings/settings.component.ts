import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { ThemeService, ThemeMode } from '../../core/services/theme.service';

@Component({
    selector: 'app-settings',
    imports: [FormsModule, ButtonModule, PasswordModule, ToastModule],
    providers: [MessageService],
    templateUrl: './settings.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrls: ['./settings.component.scss']
})
export class SettingsComponent implements OnInit {
  currentUser: User | null = null;

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  changingPassword = false;
  passwordOpen = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private messageService: MessageService,
    private router: Router,
    public themeService: ThemeService
  ) { }

  ngOnInit(): void {
    this.currentUser = this.userService.getCurrentUser();
    if (!this.currentUser) {
      this.userService.getUserProfile().subscribe({
        next: (res) => {
          this.currentUser = res.data || this.userService.getCurrentUser();
        }
      });
    }
  }

  openProfile(): void {
    this.router.navigate(['/profile'], { queryParams: { edit: '1' } });
  }

  setTheme(mode: ThemeMode): void {
    this.themeService.setTheme(mode);
  }

  get passwordsMismatch(): boolean {
    return this.confirmPassword.length > 0 && this.newPassword !== this.confirmPassword;
  }

  changePassword(): void {
    if (!this.currentPassword || !this.newPassword || this.passwordsMismatch) {
      return;
    }

    this.changingPassword = true;
    this.authService.changePassword(this.currentPassword, this.newPassword).subscribe({
      next: (response) => {
        this.changingPassword = false;
        if (response.success) {
          this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Password changed successfully' });
          this.resetPasswordForm();
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: response.error?.errorDescription || response.message || 'Failed to change password' });
        }
      },
      error: (error) => {
        this.changingPassword = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: error.error?.error?.errorDescription || 'Failed to change password' });
      }
    });
  }

  private resetPasswordForm(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
  }
}
