import { Component } from '@angular/core';
import { AuthService } from 'src/app/service/auth.service';

@Component({ selector: 'app-login', templateUrl: './login.component.html', styleUrls: ['./login.component.css'] })
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService) {}

  login() {
    this.authService.login(this.username, this.password).subscribe(response => {
      console.log('Login Successful', response);
    });
  }
}