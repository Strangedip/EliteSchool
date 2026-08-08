import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { UserService } from '../services/user.service';
import { map, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

function roleOf(userService: UserService): string {
  return userService.getCurrentUser()?.role?.toUpperCase() || '';
}

function withProfile(userService: UserService, router: Router, allow: () => boolean) {
  if (userService.getCurrentUser()) {
    return allow();
  }
  return userService.getUserProfile().pipe(
    map(() => allow()),
    catchError(() => {
      router.navigate(['/login']);
      return of(false);
    })
  );
}

export const AdminGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    router.navigate(['/dashboard']);
    return false;
  });
};

export const StaffGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'FACULTY' || role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    router.navigate(['/dashboard']);
    return false;
  });
};

export const StoreWalletGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'STUDENT' || role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    router.navigate(['/dashboard']);
    return false;
  });
};
