import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, map, of, catchError } from 'rxjs';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    // First check if we have a token and already know we're authenticated
    if (this.authService.getToken() && this.authService.getAuthStatus()) {
      return of(true);
    }

    // If we have a token but aren't sure about auth state, validate it
    // The AuthService now manages throttling and caching of validation requests
    if (this.authService.getToken()) {
      return this.authService.validateToken().pipe(
        map(response => {
          if (response.success) {
            return true;
          }
          this.authService.navigateToLogin();
          return false;
        }),
        catchError(() => {
          this.authService.navigateToLogin();
          return of(false);
        })
      );
    }

    // No token, redirect to login
    this.authService.navigateToLogin();
    return of(false);
  }
} 