import { Component, OnInit, ChangeDetectorRef, inject, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';
import { UserData } from 'src/app/models/user-data.model';
import { Gender, Role } from 'src/app/models/common.model';
import { SelectItem, MessageService } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { CardModule } from 'primeng/card';
import { RippleModule } from 'primeng/ripple';
import { FloatLabelModule } from 'primeng/floatlabel';
import { ToastModule } from 'primeng/toast';
import { finalize } from 'rxjs';
import { CommonResponseDto } from 'src/app/models/common-response.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  schemas: [NO_ERRORS_SCHEMA],
  imports: [
    CommonModule, 
    FormsModule, 
    SelectModule,
    InputTextModule,
    ButtonModule,
    PasswordModule,
    CardModule,
    RippleModule,
    FloatLabelModule,
    ToastModule
  ],
  providers: [MessageService]
})
export class RegisterComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);
  private messageService = inject(MessageService);

  loading: boolean = false;

  userData: UserData = {
    name: '',
    age: null,
    gender: '',
    email: '',
    mobileNumber: '',
    username: '',
    password: '',
    role: ''
  };

  genders: SelectItem[] = [
    { label: 'Male', value: Gender.MALE },
    { label: 'Female', value: Gender.FEMALE },
    { label: 'Other', value: Gender.OTHER }
  ];

  roles: SelectItem[] = [
    { label: 'Student', value: Role.STUDENT },
    { label: 'Faculty', value: Role.FACULTY },
    { label: 'Guest', value: Role.GUEST }
  ];

  constructor() { }

  ngOnInit(): void {
    // Initialize component
    this.userData.role = this.roles[0].value;
    this.userData.gender = this.genders[0].value;
    this.cdr.detectChanges(); // Force change detection to properly initialize dropdowns
  }

  register(): void {
    if (!this.isFormValid()) {
      this.messageService.add({
        severity: 'error',
        summary: 'Incomplete Form',
        detail: 'Please fill all required fields',
        life: 3000
      });
      return;
    }

    this.loading = true;
    this.messageService.add({
      severity: 'info',
      summary: 'Registering',
      detail: 'Creating your account...',
      life: 2000
    });
    
    this.authService.signup(this.userData)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response: CommonResponseDto<any>) => {
          if (response.success) {
            this.messageService.clear();
            this.messageService.add({
              severity: 'success',
              summary: 'Registration Complete',
              detail: 'Account created successfully',
              life: 3000
            });
            // Auto login after successful registration
            this.authService.login(this.userData.username, this.userData.password)
              .subscribe({
                next: (loginResponse: CommonResponseDto<any>) => {
                  if (loginResponse.success) {
                    this.navigateToDashboard();
                  } else {
                    this.navigateToLogin();
                  }
                },
                error: () => {
                  this.navigateToLogin();
                }
              });
          } else {
            // This handles successful HTTP requests with business logic errors
            const errorMessage = response.error 
              ? `${response.error.errorCode}: ${response.error.errorDescription}` 
              : (response.message || 'Registration failed');
            
            this.messageService.add({
              severity: 'error',
              summary: 'Registration Failed',
              detail: errorMessage,
              life: 5000
            });
          }
        },
        error: (error) => {
          // HTTP errors will be handled by the interceptor
          // This is a fallback in case the interceptor doesn't catch it
          console.error('Registration error:', error);
          
          // Check if we have a specific error response structure
          let errorMessage = 'Registration failed. Please try again.';
          
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

  private isFormValid(): boolean {
    return !!(
      this.userData.name &&
      this.userData.email &&
      this.userData.mobileNumber &&
      this.userData.username &&
      this.userData.password &&
      this.userData.role &&
      this.userData.gender
    );
  }

  navigateToLogin(): void {
    this.router.navigate(['/unauth/login']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/auth/dashboard']);
  }

  info(): void {
    console.log(this.userData);  // Display user data for debugging
  }

  testToast() {
    this.messageService.add({
      severity: 'success',
      summary: 'Test Toast',
      detail: 'This is a test toast message'
    });
  }

  validateForm(): boolean {
    // Additional validation logic beyond template-driven validation
    if (this.userData.age && this.userData.age < 12) {
      this.messageService.add({
        severity: 'error',
        summary: 'Validation Error',
        detail: 'Age must be at least 12 years'
      });
      return false;
    }
    return true;
  }
}
