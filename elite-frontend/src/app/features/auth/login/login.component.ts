import { Component, OnInit, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { CardModule } from 'primeng/card';
import { RippleModule } from 'primeng/ripple';
import { FloatLabelModule } from 'primeng/floatlabel';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { CommonResponseDto } from '../../../core/models/common-response.model';

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
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  loading = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    if (this.authService.getAuthStatus()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    const token = this.authService.getToken();
    if (token) {
      this.authService.validateToken().subscribe({
        next: (response) => {
          if (response.success) this.router.navigate(['/dashboard']);
        }
      });
    }
  }

  login(): void {
    if (!this.username || !this.password) {
      this.messageService.add({
        severity: 'error',
        summary: 'Required Fields',
        detail: 'Enter username and password',
        life: 3000
      });
      return;
    }

    this.loading = true;
    this.messageService.add({ severity: 'info', summary: 'Authenticating', detail: 'Please wait...', life: 2000 });
    
    this.authService.login(this.username, this.password)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response: CommonResponseDto<any>) => {
          if (response.success) {
            this.messageService.clear();
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Login successful', life: 2000 });
            this.router.navigate(['/dashboard']);
          } else {
            const errorMessage = response.error 
              ? `${response.error.errorCode}: ${response.error.errorDescription}` 
              : (response.message || 'Login failed');
            this.messageService.add({ severity: 'error', summary: 'Login Failed', detail: errorMessage, life: 5000 });
          }
        },
        error: (error) => {
          let errorMessage = 'Login failed. Please try again.';
          if (error.error?.error?.errorDescription) {
            errorMessage = `${error.error.error.errorCode}: ${error.error.error.errorDescription}`;
          } else if (error.error?.message) {
            errorMessage = error.error.message;
          }
          this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });
        }
      });
  }
}

