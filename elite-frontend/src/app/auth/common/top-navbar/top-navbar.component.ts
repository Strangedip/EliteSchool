import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { ToolbarModule } from 'primeng/toolbar';
import { Subscription } from 'rxjs';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [CommonModule, ButtonModule, MenuModule, RouterModule, ToolbarModule],
  templateUrl: './top-navbar.component.html',
  styleUrls: ['./top-navbar.component.scss']
})
export class TopNavbarComponent implements OnInit, OnDestroy {
  isAuthenticated = false;
  menuItems: MenuItem[] = [];
  private authSubscription: Subscription | null = null;

  constructor(private router: Router, private authService: AuthService) {}
  
  ngOnInit(): void {
    this.checkAuthStatus();
    this.setupMenuItems();
  }
  
  ngOnDestroy(): void {
    if (this.authSubscription) {
      this.authSubscription.unsubscribe();
    }
  }
  
  checkAuthStatus(): void {
    this.authSubscription = this.authService.validateToken().subscribe({
      next: (response) => {
        this.isAuthenticated = response.success;
        this.setupMenuItems();
      },
      error: () => {
        this.isAuthenticated = false;
        this.setupMenuItems();
      }
    });
  }
  
  setupMenuItems(): void {
    this.menuItems = this.isAuthenticated 
      ? [
          {
            label: 'Logout',
            icon: 'pi pi-sign-out',
            command: () => this.logout()
          }
        ]
      : [
          {
            label: 'Login',
            icon: 'pi pi-sign-in',
            command: () => this.navigateToLogin()
          },
          {
            label: 'Register',
            icon: 'pi pi-user-plus',
            command: () => this.navigateToRegister()
          }
        ];
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: (response) => {
        if (response.success) {
          this.navigateToLogin();
        }
      }
    });
  }

  navigateToLogin(): void {
    this.router.navigate(['/unauth/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/unauth/register']);
  }
} 