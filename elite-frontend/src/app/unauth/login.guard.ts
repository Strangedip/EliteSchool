import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthService } from '../core/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoginGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    // If a token exists, redirect to dashboard (user is already logged in)
    if (this.authService.getToken()) {
      this.router.navigate(['/auth/dashboard']);
      return of(false);
    }
    
    // No token, allow access to login page
    return of(true);
  }
} 