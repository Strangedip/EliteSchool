import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { map, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

const APP_ROLES = new Set(['STUDENT', 'FACULTY', 'ADMIN', 'MANAGEMENT']);

export const AuthGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const userService = inject(UserService);
  const router = inject(Router);

  const token = authService.getToken();
  if (!token || token === 'undefined' || token === 'null') {
    authService.clearToken();
    userService.clearCurrentUser();
    router.navigate(['/login']);
    return false;
  }

  return userService.getUserProfile().pipe(
    map((response) => {
      if (!response.success || !response.data) {
        authService.clearToken();
        userService.clearCurrentUser();
        router.navigate(['/login']);
        return false;
      }

      const role = (response.data.role || '').toUpperCase();
      if (!APP_ROLES.has(role)) {
        authService.clearToken();
        userService.clearCurrentUser();
        router.navigate(['/home']);
        return false;
      }

      return true;
    }),
    catchError(() => {
      authService.clearToken();
      userService.clearCurrentUser();
      router.navigate(['/login']);
      return of(false);
    })
  );
};
