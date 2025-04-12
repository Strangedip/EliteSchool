import { Component } from '@angular/core';
import { UserService } from 'src/app/service/user.service';

@Component({ selector: 'app-register', templateUrl: './register.component.html', styleUrls: ['./register.component.scss'] })
export class RegisterComponent {
  userData: any = {};

  constructor(private userService: UserService) {}

  register() {
    this.userService.createUser(this.userData).subscribe(response => {
      console.log('User Registered', response);
    });
  }
}