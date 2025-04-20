import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonResponseDto } from 'src/app/model/common-response.model';
import { AuthService } from 'src/app/service/auth.service';
import { ToastService } from 'src/app/service/toast.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private toastService: ToastService, private router: Router, private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.validateToken().subscribe(
      (response: CommonResponseDto<any>) => {
        if (response.success) {
          this.navigateToDashboard();
        }
      });
  }

  navigateToDashboard() {
    this.router.navigate(['/auth/dashboard']);
  }

  login() {
    this.authService.login(this.username, this.password).subscribe(
      (response: CommonResponseDto<any>) => {
        if (response.success) {
          // Show success message if login is successful
          this.toastService.showSuccess(response.message || 'Login successful');
          this.navigateToLandingPage();
        } else {
          // Show error message if the response indicates failure
          const errorMessage = response.error ? `${response.error.errorCode}: ${response.error.errorDescription}` : 'Login failed';
          this.toastService.showError(errorMessage);
        }
      },
      (error) => {
        // Show a generic error message in case of network or server error
        let errorResponse = error.error && error.error.error ? error.error.error : null;
        const errorMessage = errorResponse ? `${errorResponse.errorCode}: ${errorResponse.errorDescription}` : 'An error occurred. Please try again later.';
        this.toastService.showError(errorMessage);
      }
    );
  }

  navigateToLandingPage() {
    this.router.navigate(['/auth/dashboard']);
  }
}
