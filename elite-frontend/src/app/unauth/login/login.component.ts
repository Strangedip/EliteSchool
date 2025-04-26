import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { CommonResponseDto } from 'src/app/model/common-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { ToastService } from 'src/app/service/toast.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { CardModule } from 'primeng/card';
import { RippleModule } from 'primeng/ripple';
import { FloatLabelModule } from 'primeng/floatlabel';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  schemas: [NO_ERRORS_SCHEMA],
  imports: [
    CommonModule, 
    FormsModule, 
    InputTextModule,
    ButtonModule,
    PasswordModule,
    CardModule,
    RippleModule,
    FloatLabelModule
  ]
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  loading: boolean = false;

  constructor(
    private toastService: ToastService,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.validateToken().subscribe({
      next: (response: CommonResponseDto<any>) => {
        if (response.success) {
          this.navigateToDashboard();
        }
      }
    });
  }

  navigateToDashboard() {
    this.router.navigate(['/auth/dashboard']);
  }

  login() {
    if (!this.username || !this.password) {
      this.toastService.showError('Please enter both username and password');
      return;
    }

    this.loading = true;
    this.authService.login(this.username, this.password).subscribe({
      next: (response: CommonResponseDto<any>) => {
        this.loading = false;
        if (response.success) {
          this.toastService.showSuccess(response.message || 'Login successful');
          this.navigateToLandingPage();
        } else {
          const errorMessage = response.error ? `${response.error.errorCode}: ${response.error.errorDescription}` : 'Login failed';
          this.toastService.showError(errorMessage);
        }
      },
      error: (error) => {
        this.loading = false;
        let errorResponse = error.error && error.error.error ? error.error.error : null;
        const errorMessage = errorResponse ? `${errorResponse.errorCode}: ${errorResponse.errorDescription}` : 'An error occurred. Please try again later.';
        this.toastService.showError(errorMessage);
      }
    });
  }

  navigateToLandingPage() {
    this.router.navigate(['/auth/dashboard']);
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
