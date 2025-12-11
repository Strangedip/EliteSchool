import { Component, OnInit, ChangeDetectorRef, inject, NO_ERRORS_SCHEMA } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
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
import { SelectItem, MessageService } from 'primeng/api';
import { finalize } from 'rxjs';
import { AuthService, UserRegistrationData } from '../../../core/services/auth.service';
import { CommonResponseDto } from '../../../core/models/common-response.model';

export enum Gender { MALE = 'MALE', FEMALE = 'FEMALE', OTHER = 'OTHER' }
export enum Role { ADMIN = 'ADMIN', FACULTY = 'FACULTY', STUDENT = 'STUDENT', GUEST = 'GUEST' }

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  schemas: [NO_ERRORS_SCHEMA],
  imports: [
    CommonModule, FormsModule, SelectModule, InputTextModule, ButtonModule,
    PasswordModule, CardModule, RippleModule, FloatLabelModule, ToastModule, RouterModule
  ],
  providers: [MessageService]
})
export class RegisterComponent implements OnInit {
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);
  private messageService = inject(MessageService);

  loading = false;
  userData: UserRegistrationData = {
    name: '', age: null as any, gender: '', email: '',
    mobileNumber: '', username: '', password: '', role: ''
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

  ngOnInit(): void {
    this.userData.role = this.roles[0].value;
    this.userData.gender = this.genders[0].value;
    this.cdr.detectChanges();
  }

  register(): void {
    if (!this.isFormValid()) {
      this.messageService.add({ severity: 'error', summary: 'Incomplete Form', detail: 'Please fill all required fields', life: 3000 });
      return;
    }

    this.loading = true;
    this.messageService.add({ severity: 'info', summary: 'Registering', detail: 'Creating your account...', life: 2000 });
    
    this.authService.signup(this.userData)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response: CommonResponseDto<any>) => {
          if (response.success) {
            this.messageService.clear();
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Account created', life: 2000 });
            this.authService.login(this.userData.username, this.userData.password).subscribe({
              next: (loginResponse) => {
                this.router.navigate([loginResponse.success ? '/dashboard' : '/login']);
              },
              error: () => this.router.navigate(['/login'])
            });
          } else {
            const errorMessage = response.error 
              ? `${response.error.errorCode}: ${response.error.errorDescription}` 
              : (response.message || 'Registration failed');
            this.messageService.add({ severity: 'error', summary: 'Failed', detail: errorMessage, life: 5000 });
          }
        },
        error: (error) => {
          let errorMessage = 'Registration failed. Please try again.';
          if (error.error?.error?.errorDescription) {
            errorMessage = `${error.error.error.errorCode}: ${error.error.error.errorDescription}`;
          } else if (error.error?.message) {
            errorMessage = error.error.message;
          }
          this.messageService.add({ severity: 'error', summary: 'Error', detail: errorMessage, life: 5000 });
        }
      });
  }

  private isFormValid(): boolean {
    return !!(this.userData.name && this.userData.email && this.userData.mobileNumber &&
              this.userData.username && this.userData.password && this.userData.role && this.userData.gender);
  }
}

