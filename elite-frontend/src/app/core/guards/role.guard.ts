import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { UserService } from '../services/user.service';
import { ToastService } from '../services/toast.service';
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

function deny(router: Router, toast: ToastService, message: string): false {
  toast.showError(message);
  router.navigate(['/dashboard']);
  return false;
}

export const AdminGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);
  const toast = inject(ToastService);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    return deny(router, toast, 'Only admins can open Manage Users and Audit.');
  });
};

export const StaffGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);
  const toast = inject(ToastService);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'FACULTY' || role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    return deny(router, toast, 'Only faculty and admins can open Nominations.');
  });
};

export const RewardsPointsGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);
  const toast = inject(ToastService);

  return withProfile(userService, router, () => {
    const role = roleOf(userService);
    if (role === 'STUDENT' || role === 'ADMIN' || role === 'MANAGEMENT') {
      return true;
    }
    return deny(router, toast, 'Rewards and Elite Points are for students and admins.');
  });
};
