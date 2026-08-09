import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';
import { ToastService } from '../../../core/services/toast.service';
import { finalize } from 'rxjs';
import { BrandMarkComponent } from '../../../shared/brand-mark.component';

@Component({
    selector: 'app-forgot-password',
    imports: [CommonModule, ReactiveFormsModule, RouterModule, InputTextModule, ButtonModule, BrandMarkComponent],
    templateUrl: './forgot-password.component.html',
    changeDetection: ChangeDetectionStrategy.Eager
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  loading = false;
  emailSent = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router,
    public themeService: ThemeService
  ) {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }

  get email() {
    return this.forgotPasswordForm.get('email');
  }

  onSubmit(): void {
    if (this.forgotPasswordForm.invalid) {
      this.markFormGroupTouched();
      this.toastService.showError('Please enter a valid email address');
      return;
    }

    this.loading = true;
    const email = this.forgotPasswordForm.value.email;

    this.authService.forgotPassword(email)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.emailSent = true;
          this.toastService.showSuccess('Password reset email sent! Check your inbox.');
        },
        error: (error) => {
          this.toastService.showError(error.error?.userMessage || 'Failed to send reset email');
        }
      });
  }

  backToLogin(): void {
    this.router.navigate(['/login']);
  }

  private markFormGroupTouched(): void {
    Object.keys(this.forgotPasswordForm.controls).forEach(key => {
      this.forgotPasswordForm.get(key)?.markAsTouched();
    });
  }
}
