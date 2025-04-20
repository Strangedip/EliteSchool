import { AuthService } from 'src/app/service/auth.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent {
  constructor(private AuthService: AuthService, private router: Router) { }

  logout() {
    this.AuthService.logout().subscribe(
      (response) => {
        if (response.success) {
          this.navigateToLogin();
        }
      });
  }

  navigateToLogin() {
    this.router.navigate(['/unauth/login']);
  }

}
