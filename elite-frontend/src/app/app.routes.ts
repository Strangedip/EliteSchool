import { Routes } from '@angular/router';
import { LoginComponent } from './unauth/login/login.component';
import { RegisterComponent } from './unauth/register/register.component';
import { UnauthLayoutComponent } from './unauth/unauth-layout.component';
import { TestToastComponent } from './test-toast.component';

export const routes: Routes = [
  { path: '', redirectTo: '/unauth/login', pathMatch: 'full' },
  { 
    path: 'unauth', 
    component: UnauthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ]
  },
  { path: 'test-toast', component: TestToastComponent },
  // Add more routes as needed
]; 