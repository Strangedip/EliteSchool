import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../service/user.service';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-info',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="user-info-container" *ngIf="user">
      <div class="user-avatar" *ngIf="showAvatar" (click)="navigateToProfile()">
        <div class="avatar-placeholder" *ngIf="!user.profilePicture">
          {{ getUserInitials() }}
        </div>
        <img *ngIf="user.profilePicture" [src]="user.profilePicture" alt="Profile Picture">
      </div>
      
      <div class="user-details" *ngIf="showDetails">
        <div class="user-name" (click)="navigateToProfile()">{{ user.name }}</div>
        <div class="user-role" *ngIf="showRole">{{ user.role }}</div>
      </div>
      
      <button *ngIf="showLogout" class="logout-button" (click)="logout()">
        Logout
      </button>
    </div>
  `,
  styles: [`
    .user-info-container {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 8px;
    }
    
    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      cursor: pointer;
    }
    
    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background-color: #4a90e2;
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
    }
    
    .user-avatar img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    
    .user-details {
      display: flex;
      flex-direction: column;
    }
    
    .user-name {
      font-weight: 500;
      cursor: pointer;
    }
    
    .user-name:hover {
      text-decoration: underline;
    }
    
    .user-role {
      font-size: 0.8rem;
      color: #666;
    }
    
    .logout-button {
      padding: 6px 12px;
      background-color: #f44336;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.8rem;
    }
  `]
})
export class UserInfoComponent implements OnInit {
  @Input() showAvatar: boolean = true;
  @Input() showDetails: boolean = true;
  @Input() showRole: boolean = true;
  @Input() showLogout: boolean = false;
  
  user: User | null = null;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.currentUser$.subscribe(user => {
      this.user = user;
    });
  }

  getUserInitials(): string {
    if (!this.user || !this.user.name) return '';
    
    const nameParts = this.user.name.split(' ');
    if (nameParts.length === 1) {
      return nameParts[0].charAt(0).toUpperCase();
    } else {
      return (
        nameParts[0].charAt(0).toUpperCase() + 
        nameParts[nameParts.length - 1].charAt(0).toUpperCase()
      );
    }
  }

  navigateToProfile(): void {
    this.router.navigate(['/auth/profile']);
  }

  logout(): void {
    // Implement logout functionality
    this.userService.clearCurrentUser();
    this.router.navigate(['/unauth/login']);
  }
} 