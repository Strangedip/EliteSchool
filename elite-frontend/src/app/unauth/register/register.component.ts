import { Component, OnInit, ChangeDetectorRef, inject, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';
import { ToastService } from 'src/app/service/toast.service';
import { UserData } from 'src/app/model/user-data.model';
import { Gender, Role } from 'src/app/model/common.model';
import { SelectItem } from 'primeng/api';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { CardModule } from 'primeng/card';
import { RippleModule } from 'primeng/ripple';
import { FloatLabelModule } from 'primeng/floatlabel';

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
    FloatLabelModule
  ]
})
export class RegisterComponent implements OnInit {
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);

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
      this.toastService.showError('Please fill in all required fields correctly');
      return;
    }

    this.loading = true;
    this.toastService.showInfo('Registering user...');
    this.authService.signup(this.userData).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.success) {
          this.toastService.showSuccess(response.message || 'Registration successful');
          this.navigateToLogin();
        } else {
          const errorMessage = response.error
            ? `${response.error.errorCode}: ${response.error.errorDescription}`
            : 'Registration failed';
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
    this.router.navigate(['/login']);
  }

  info(): void {
    console.log(this.userData);  // Display user data for debugging
  }
}
