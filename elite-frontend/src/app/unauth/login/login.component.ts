import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
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
import { finalize } from 'rxjs';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

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
    FloatLabelModule,
    RouterModule,
    ToastModule
  ],
  providers: [MessageService]
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  loading: boolean = false;

  constructor(
    private toastService: ToastService,
    private router: Router,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    // First check if user is already authenticated based on local state
    if (this.authService.getAuthStatus()) {
      this.navigateToDashboard();
      return;
    }

    // Otherwise, validate token if one exists - important for cases where 
    // an authenticated user tries to access the login page directly
    const token = this.authService.getToken();
    if (token) {
      this.authService.validateToken().subscribe({
        next: (response: CommonResponseDto<any>) => {
          if (response.success) {
            this.navigateToDashboard();
          }
        },
        error: (error) => {
          console.log('Token validation failed:', error);
          // We don't show error toast for token validation failures
        }
      });
    }
  }

  login() {
    if (!this.username || !this.password) {
      // Use direct message service for more reliable toast
      this.messageService.add({
        severity: 'error',
        summary: 'Required Fields',
        detail: 'Enter username and password',
        life: 3000
      });
      return;
    }

    this.loading = true;
    this.messageService.add({
      severity: 'info',
      summary: 'Authenticating',
      detail: 'Please wait...',
      life: 2000
    });
    
    this.authService.login(this.username, this.password)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response: CommonResponseDto<any>) => {
          if (response.success) {
            this.messageService.clear();
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Login successful',
              life: 3000
            });
            this.navigateToLandingPage();
          } else {
            // This handles successful HTTP requests with business logic errors
            const errorMessage = response.error 
              ? `${response.error.errorCode}: ${response.error.errorDescription}` 
              : (response.message || 'Login failed');
            
            this.messageService.add({
              severity: 'error',
              summary: 'Login Failed',
              detail: errorMessage,
              life: 5000
            });
          }
        },
        error: (error) => {
          // HTTP errors will be handled by the interceptor
          // This is a fallback in case the interceptor doesn't catch it
          console.error('Login error:', error);
          
          // Check if we have a specific error response structure
          let errorMessage = 'Login failed. Please try again.';
          
          if (error.error) {
            if (error.error.error && error.error.error.errorDescription) {
              errorMessage = `${error.error.error.errorCode}: ${error.error.error.errorDescription}`;
            } else if (error.error.message) {
              errorMessage = error.error.message;
            }
          }
          
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: errorMessage,
            life: 5000
          });
        }
      });
  }

  navigateToDashboard() {
    this.router.navigate(['/auth/dashboard']);
  }

  navigateToLandingPage() {
    this.router.navigate(['/auth/dashboard']);
  }

  navigateToRegister() {
    this.router.navigate(['/unauth/register']);
  }
}
