import { Routes } from '@angular/router';
import { LoginComponent } from './unauth/login/login.component';
import { RegisterComponent } from './unauth/register/register.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  // Add more routes as needed
]; 