import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../service/user.service';
import { User } from '../../models/user.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="user-profile-container">
      <div class="profile-header">
        <h2>User Profile</h2>
        <p *ngIf="currentUser">Welcome, {{ currentUser.name }}</p>
      </div>

      <div class="profile-details" *ngIf="currentUser">
        <div class="profile-info">
          <p><strong>Username:</strong> {{ currentUser.username }}</p>
          <p><strong>Email:</strong> {{ currentUser.email }}</p>
          <p><strong>Role:</strong> {{ currentUser.role }}</p>
          <p *ngIf="currentUser.mobileNumber"><strong>Mobile:</strong> {{ currentUser.mobileNumber }}</p>
        </div>

        <div class="profile-form-container" *ngIf="showForm">
          <h3>Update Profile</h3>
          <form [formGroup]="profileForm" (ngSubmit)="updateProfile()">
            <div class="form-group">
              <label for="name">Name</label>
              <input type="text" id="name" formControlName="name">
            </div>
            
            <div class="form-group">
              <label for="email">Email</label>
              <input type="email" id="email" formControlName="email">
            </div>
            
            <div class="form-group">
              <label for="mobileNumber">Mobile Number</label>
              <input type="tel" id="mobileNumber" formControlName="mobileNumber">
            </div>
            
            <div class="form-actions">
              <button type="submit" [disabled]="profileForm.invalid || isSubmitting">Save Changes</button>
              <button type="button" (click)="cancelEdit()">Cancel</button>
            </div>
          </form>
        </div>
        
        <button *ngIf="!showForm" (click)="toggleEditForm()">Edit Profile</button>
      </div>
      
      <div class="loading-container" *ngIf="isLoading">
        <p>Loading profile information...</p>
      </div>
      
      <div class="error-container" *ngIf="errorMessage">
        <p class="error-message">{{ errorMessage }}</p>
      </div>
    </div>
  `,
  styles: [`
    .user-profile-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
    }
    
    .profile-header {
      margin-bottom: 20px;
      border-bottom: 1px solid #eee;
      padding-bottom: 10px;
    }
    
    .profile-details {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    
    .profile-info {
      background-color: #f9f9f9;
      padding: 15px;
      border-radius: 5px;
    }
    
    .profile-form-container {
      margin-top: 20px;
    }
    
    .form-group {
      margin-bottom: 15px;
    }
    
    .form-group label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
    }
    
    .form-group input {
      width: 100%;
      padding: 8px;
      border: 1px solid #ddd;
      border-radius: 4px;
    }
    
    .form-actions {
      display: flex;
      gap: 10px;
      margin-top: 20px;
    }
    
    button {
      padding: 8px 16px;
      background-color: #4a90e2;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    
    button:disabled {
      background-color: #cccccc;
      cursor: not-allowed;
    }
    
    button[type="button"] {
      background-color: #f44336;
    }
    
    .error-message {
      color: #f44336;
      font-weight: 500;
    }
  `]
})
export class UserProfileComponent implements OnInit {
  currentUser: User | null = null;
  profileForm!: FormGroup;
  showForm = false;
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private userService: UserService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    // Subscribe to user data from the service
    this.userService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if (user) {
        this.initForm(user);
      }
    });
    
    // Refresh user profile data from API
    this.refreshUserProfile();
  }

  refreshUserProfile(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.userService.getUserProfile().subscribe({
      next: (response) => {
        this.isLoading = false;
        if (!response.success) {
          this.errorMessage = response.message || 'Failed to load profile';
        }
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Error loading profile: ' + (error.message || 'Unknown error');
      }
    });
  }

  initForm(user: User): void {
    this.profileForm = this.fb.group({
      name: [user.name, Validators.required],
      email: [user.email, [Validators.required, Validators.email]],
      mobileNumber: [user.mobileNumber || '']
    });
  }

  toggleEditForm(): void {
    this.showForm = !this.showForm;
    if (this.showForm && this.currentUser) {
      this.initForm(this.currentUser);
    }
  }

  cancelEdit(): void {
    this.showForm = false;
  }

  updateProfile(): void {
    if (this.profileForm.invalid) return;
    
    this.isSubmitting = true;
    this.errorMessage = '';
    
    const updatedData = this.profileForm.value;
    
    this.userService.updateUserProfile(updatedData).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.success) {
          this.showForm = false;
        } else {
          this.errorMessage = response.message || 'Failed to update profile';
        }
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = 'Error updating profile: ' + (error.message || 'Unknown error');
      }
    });
  }
} 