import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  forgotPasswordForm: FormGroup;
  loading = false;
  emailSent = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.forgotPasswordForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
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
        next: (response) => {
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

