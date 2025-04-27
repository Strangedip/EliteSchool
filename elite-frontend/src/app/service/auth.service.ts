import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError, shareReplay } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { UserService } from './user.service';
import { CommonResponseDto } from '../models/common-response.model';
import { LoginResponseDto, User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private TOKEN_KEY = 'auth_token';
  private tokenValidationInProgress: Observable<any> | null = null;
  private lastValidationTime = 0;
  private validationThrottleTime = 15000; // 15 seconds
  
  constructor(
    private http: HttpClient, 
    private router: Router,
    private userService: UserService
  ) {
    // Check authentication state on service initialization
    this.checkAuthState();
  }

  private checkAuthState(): void {
    // Check for existing token in localStorage
    const token = this.getToken();
    if (token) {
      this.validateToken().subscribe({
        next: (response) => {
          this.isAuthenticatedSubject.next(response.success);
        },
        error: () => {
          this.clearToken();
          this.userService.clearCurrentUser();
          this.isAuthenticatedSubject.next(false);
        }
      });
    } else {
      this.isAuthenticatedSubject.next(false);
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    this.isAuthenticatedSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.isAuthenticatedSubject.next(false);
  }

  login(username: string, password: string): Observable<CommonResponseDto<LoginResponseDto>> {
    return this.http.post<CommonResponseDto<LoginResponseDto>>(
      `${this.apiUrl}/login`, 
      { username, password }
    ).pipe(
      tap((response: CommonResponseDto<LoginResponseDto>) => {
        if (response.success && response.data) {
          // Save token
          this.saveToken(response.data.token);
          
          // Save user data to user service
          if (response.data.user) {
            this.userService.setCurrentUser(response.data.user);
          }
        }
      })
    );
  }
  
  signup(userData: any): Observable<CommonResponseDto<LoginResponseDto>> {
    return this.http.post<CommonResponseDto<LoginResponseDto>>(
      `${this.apiUrl}/signup`, 
      userData
    ).pipe(
      tap((response: CommonResponseDto<LoginResponseDto>) => {
        if (response.success && response.data) {
          // Save token
          this.saveToken(response.data.token);
          
          // Save user data to user service
          if (response.data.user) {
            this.userService.setCurrentUser(response.data.user);
          }
        }
      })
    );
  }

  validateToken(): Observable<CommonResponseDto<User>> {
    // Use the token from localStorage in the Authorization header
    const headers = this.getAuthHeaders();
    if (!headers.has('Authorization')) {
      return of({ success: false, message: 'No token available' });
    }
    
    // Check if we need to throttle validation requests
    const now = Date.now();
    if (this.tokenValidationInProgress) {
      return this.tokenValidationInProgress;
    }
    
    if (now - this.lastValidationTime < this.validationThrottleTime) {
      // Return the current auth state if we've validated recently
      return of({ 
        success: this.isAuthenticatedSubject.value, 
        message: 'Using cached authentication state' 
      });
    }
    
    // Create new validation request and share it
    this.lastValidationTime = now;
    this.tokenValidationInProgress = this.http.get<CommonResponseDto<User>>(
      `${this.apiUrl}/validate-token`, 
      { headers }
    ).pipe(
      tap((response: CommonResponseDto<User>) => {
        if (!response.success) {
          this.clearToken();
          this.userService.clearCurrentUser();
        } else if (response.data) {
          // Update user data if validation returns user info
          this.userService.setCurrentUser(response.data);
        }
      }),
      catchError((error) => {
        this.clearToken();
        this.userService.clearCurrentUser();
        return throwError(() => error);
      }),
      shareReplay(1),
      tap(() => {
        // Clear the in-progress observable after completion
        setTimeout(() => {
          this.tokenValidationInProgress = null;
        }, 0);
      })
    );
    
    return this.tokenValidationInProgress;
  }
  
  logout(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/logout`, {}, { headers })
      .pipe(
        tap((response: any) => {
          this.clearToken();
          this.userService.clearCurrentUser();
        }),
        catchError((error) => {
          this.clearToken();
          this.userService.clearCurrentUser();
          return throwError(() => error);
        })
      );
  }

  getAuthStatus(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  navigateToLogin(): void {
    this.router.navigate(['/unauth/login']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/auth/dashboard']);
  }
}