import { inject } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AuthService } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { CommonResponseDto } from '../models/common-response.model';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  const authService = inject(AuthService);
  const userService = inject(UserService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred';

      if (error.error && error.error.success === false) {
        const response = error.error as CommonResponseDto<any>;
        // Prefer friendly top-level message; never surface raw error codes in the UI
        if (response.message) {
          errorMessage = response.message;
        } else if (response.error?.errorDescription) {
          errorMessage = response.error.errorDescription;
        }
        toastService.showError(errorMessage);
      } else if (error.status === 0) {
        toastService.showError('Network error. Please check your connection.');
      } else if (error.status === 401) {
        authService.clearToken();
        userService.clearCurrentUser();
        toastService.showError('Session expired. Please log in again.');
        router.navigate(['/login']);
      } else if (error.status === 403) {
        toastService.showError('You don\'t have permission to do that.');
      } else if (error.status === 404) {
        toastService.showError('Resource not found.');
      } else if (error.status === 500) {
        toastService.showError('Server error. Please try again later.');
      } else {
        if (error.error && typeof error.error === 'object') {
          if (error.error.message) {
            errorMessage = error.error.message;
          } else if (error.error.error?.errorDescription) {
            errorMessage = error.error.error.errorDescription;
          } else if (error.message) {
            errorMessage = error.message;
          }
        }
        toastService.showError(errorMessage);
      }

      return throwError(() => error);
    })
  );
};
