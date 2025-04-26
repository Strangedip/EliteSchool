import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-unauth-navbar',
  standalone: true,
  imports: [CommonModule, ButtonModule, MenuModule, RouterModule],
  templateUrl: './unauth-navbar.component.html',
  styleUrls: ['./unauth-navbar.component.scss']
})
export class UnauthNavbarComponent {
  menuItems: MenuItem[] = [];

  constructor(private router: Router) {
    this.menuItems = [
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

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
