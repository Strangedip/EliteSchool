import { Routes } from '@angular/router';
import { LoginComponent } from './unauth/login/login.component';
import { RegisterComponent } from './unauth/register/register.component';
import { UnauthLayoutComponent } from './unauth/unauth-layout.component';
import { TestToastComponent } from './test-toast.component';
import { AuthGuard } from './auth/auth.guard';
export const routes: Routes = [
  { path: '', redirectTo: '/auth/dashboard', pathMatch: 'full' },
  { 
    path: 'unauth', 
    component: UnauthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ]
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule),
    canActivate: [AuthGuard]
  },
  { path: 'test-toast', component: TestToastComponent },
  // Add more routes as needed
]; 