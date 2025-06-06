import { Routes } from '@angular/router';
import { LoginComponent } from './unauth/login/login.component';
import { RegisterComponent } from './unauth/register/register.component';
import { UnauthLayoutComponent } from './unauth/unauth-layout.component';
import { TestToastComponent } from './test-toast.component';
import { AuthGuard } from './auth/auth.guard';
import { LoginGuard } from './unauth/login.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/unauth/login', pathMatch: 'full' },
  { 
    path: 'unauth', 
    component: UnauthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent, canActivate: [LoginGuard] },
      { path: 'register', component: RegisterComponent, canActivate: [LoginGuard] },
    ]
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule),
    canActivate: [AuthGuard]
  },
]; 