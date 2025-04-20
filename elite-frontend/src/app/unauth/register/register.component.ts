import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';
import { UserService } from 'src/app/service/user.service';
import { ToastService } from 'src/app/service/toast.service';  // Import ToastService
import { UserData } from 'src/app/model/user-data.model';
import { Gender, Role } from 'src/app/model/common.model';
import { SelectItem } from 'primeng/api';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
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

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private toastService: ToastService,  // Inject ToastService
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // Initialize component
  }

  onDropdownChange(event: any, field: 'gender' | 'role'): void {
    this.userData[field] = event.value;
    // Force UI update
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 0);
  }

  register(): void {
    this.toastService.showInfo('Registering user...');
    this.authService.signup(this.userData).subscribe({
      next: (response) => {
        if (response.success) {
          this.toastService.showSuccess(response.message || 'Registration successful');

        } else {
          const errorMessage = response.error
            ? `${response.error.errorCode}: ${response.error.errorDescription}`
            : 'Registration failed';
          this.toastService.showError(errorMessage);
        }
      },
      error: (error) => {
        let errorResponse = error.error && error.error.error ? error.error.error : null;
        const errorMessage = errorResponse ? `${errorResponse.errorCode}: ${errorResponse.errorDescription}` : 'An error occurred. Please try again later.';
        this.toastService.showError(errorMessage);
      }
    });
  }

  info(): void {
    console.log(this.userData);  // Display user data for debugging
  }
}
