import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-unauth-navbar',
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
    this.router.navigate(['/unauth/login']);
  }

  navigateToRegister() {
    this.router.navigate(['/unauth/register']);
  }
}
