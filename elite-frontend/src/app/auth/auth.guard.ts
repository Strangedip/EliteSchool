import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { AuthService } from '../core/services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): Observable<boolean> {
    // Simply check if token exists - no validation logic here
    if (this.authService.getToken()) {
      return of(true);
    }
    
    // No token found, redirect to login
    this.router.navigate(['/unauth/login']);
    return of(false);
  }
} 