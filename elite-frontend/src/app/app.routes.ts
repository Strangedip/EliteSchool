import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { GuestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  // Default redirect
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  
  // Public routes
  {
    path: 'home',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent),
    title: 'EliteSchool - Home'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent),
    canActivate: [GuestGuard],
    title: 'Login - EliteSchool'
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent),
    canActivate: [GuestGuard],
    title: 'Register - EliteSchool'
  },
  
  // Protected routes with layout
  {
    path: '',
    loadComponent: () => import('./layouts/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    canActivate: [AuthGuard],
    children: [
      { 
        path: 'dashboard', 
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        title: 'Dashboard - EliteSchool'
      },
      { 
        path: 'tasks', 
        loadComponent: () => import('./features/tasks/taskboard.component').then(m => m.TaskboardComponent),
        title: 'Tasks - EliteSchool'
      },
      { 
        path: 'store', 
        loadComponent: () => import('./features/store/store.component').then(m => m.StoreComponent),
        title: 'Store - EliteSchool'
      },
      { 
        path: 'wallet', 
        loadComponent: () => import('./features/wallet/wallet.component').then(m => m.WalletComponent),
        title: 'Wallet - EliteSchool'
      },
      { 
        path: 'profile', 
        loadComponent: () => import('./features/profile/user-profile.component').then(m => m.UserProfileComponent),
        title: 'Profile - EliteSchool'
      },
      { 
        path: 'games',
        loadChildren: () => import('./features/games/games.routes').then(m => m.GAMES_ROUTES),
        title: 'Games - EliteSchool'
      },
      { 
        path: 'settings', 
        loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent),
        title: 'Settings - EliteSchool'
      },
    ]
  },
  
  // 404 Page
  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found.component').then(m => m.NotFoundComponent),
    title: 'Page Not Found - EliteSchool'
  }
];
