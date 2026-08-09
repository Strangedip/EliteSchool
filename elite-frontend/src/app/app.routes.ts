import { Routes } from '@angular/router';
import { AuthGuard, GuestCanMatch } from './core/guards/auth.guard';
import { AdminGuard, StaffGuard, StoreWalletGuard } from './core/guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  {
    path: 'home',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent),
    title: 'EliteSchool - Home'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent),
    title: 'Login - EliteSchool'
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent),
    title: 'Register - EliteSchool'
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
    title: 'Forgot Password - EliteSchool'
  },
  {
    path: 'reset-password',
    loadComponent: () => import('./features/auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent),
    title: 'Reset Password - EliteSchool'
  },

  /* Visitors: public chrome (no app sidebar) */
  {
    path: 'docs',
    canMatch: [GuestCanMatch],
    loadComponent: () => import('./features/docs/docs.component').then(m => m.DocsComponent),
    title: 'Documentation - EliteSchool'
  },
  {
    path: 'games',
    canMatch: [GuestCanMatch],
    loadChildren: () => import('./features/games/games.routes').then(m => m.GAMES_ROUTES),
    title: 'Games - EliteSchool'
  },

  /* Signed-in shell: sidebar + top header stay visible */
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
        path: 'courses',
        loadComponent: () => import('./features/courses/courses.component').then(m => m.CoursesComponent),
        title: 'Courses - EliteSchool'
      },
      {
        path: 'support',
        loadComponent: () => import('./features/support/support.component').then(m => m.SupportComponent),
        title: 'Support - EliteSchool'
      },
      {
        path: 'nominations',
        loadComponent: () => import('./features/nominations/nominations.component').then(m => m.NominationsComponent),
        canActivate: [StaffGuard],
        title: 'Nominations - EliteSchool'
      },
      {
        path: 'store',
        loadComponent: () => import('./features/store/store.component').then(m => m.StoreComponent),
        canActivate: [StoreWalletGuard],
        title: 'Rewards - EliteSchool'
      },
      {
        path: 'wallet',
        loadComponent: () => import('./features/wallet/wallet.component').then(m => m.WalletComponent),
        canActivate: [StoreWalletGuard],
        title: 'Elite Points - EliteSchool'
      },
      {
        path: 'leaderboard',
        loadComponent: () => import('./features/leaderboard/leaderboard.component').then(m => m.LeaderboardComponent),
        title: 'Leaderboard - EliteSchool'
      },
      {
        path: 'profile',
        loadComponent: () => import('./features/profile/user-profile.component').then(m => m.UserProfileComponent),
        title: 'Profile - EliteSchool'
      },
      {
        path: 'settings',
        loadComponent: () => import('./features/settings/settings.component').then(m => m.SettingsComponent),
        title: 'Settings - EliteSchool'
      },
      {
        path: 'admin/users',
        loadComponent: () => import('./features/admin/admin-users.component').then(m => m.AdminUsersComponent),
        canActivate: [AdminGuard],
        title: 'Manage Users - EliteSchool'
      },
      {
        path: 'admin/audit',
        loadComponent: () => import('./features/admin/admin-audit.component').then(m => m.AdminAuditComponent),
        canActivate: [AdminGuard],
        title: 'School Audit - EliteSchool'
      },
      {
        path: 'docs',
        loadComponent: () => import('./features/docs/docs.component').then(m => m.DocsComponent),
        data: { embedded: true },
        title: 'Documentation - EliteSchool'
      },
      {
        path: 'games',
        loadChildren: () => import('./features/games/games.routes').then(m => m.GAMES_ROUTES),
        data: { embedded: true },
        title: 'Games - EliteSchool'
      },
    ]
  },

  {
    path: '**',
    loadComponent: () => import('./features/not-found/not-found.component').then(m => m.NotFoundComponent),
    title: 'Page Not Found - EliteSchool'
  }
];
