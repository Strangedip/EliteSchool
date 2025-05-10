import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError, shareReplay, map } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { UserService } from './user.service';
import { CommonResponseDto } from '../models/common-response.model';
import { LoginResponseDto, User } from '../models/user.model';

/**
 * Interface for user registration data
 */
export interface UserRegistrationData {
  name: string;
  email: string;
  username: string;
  password: string;
  role: string;
  gender: string;
  mobileNumber?: string;
  age?: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private TOKEN_KEY = 'Authorization';
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
    this.isAuthenticatedSubject.next(!!token);
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
    // Also save to the old location for compatibility
    localStorage.setItem('token', token);
    this.isAuthenticatedSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY) || localStorage.getItem('token');
  }

  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('token'); // Also clear old token key for compatibility
    this.isAuthenticatedSubject.next(false);
  }

  login(username: string, password: string): Observable<CommonResponseDto<LoginResponseDto>> {
    return this.http.post<CommonResponseDto<any>>(
      `${this.apiUrl}/login`, 
      { username, password }
    ).pipe(
      map(response => {
        if (response.success && response.data) {
          // Extract token and user from response
          const { token, user } = response.data;
          
          // Save token
          this.saveToken(token);
          
          // Save user data to user service if available
          if (user) {
            this.userService.setCurrentUser(user);
          }
          
          // Format as LoginResponseDto
          return {
            success: response.success,
            message: response.message,
            data: {
              token: token,
              user: user
            }
          };
        }
        return response;
      })
    );
  }
  
  signup(userData: UserRegistrationData): Observable<CommonResponseDto<LoginResponseDto>> {
    userData.role = userData.role.toUpperCase(); // Ensure role is in uppercase
    userData.gender = userData.gender.toUpperCase(); // Ensure gender is in uppercase
    
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
    
    // Simple token validation
    return this.http.get<CommonResponseDto<User>>(
      `${this.apiUrl}/validate-token`, 
      { headers }
    ).pipe(
      tap((response: CommonResponseDto<User>) => {
        if (response.success && response.data) {
          // Update user data if validation returns user info
          this.userService.setCurrentUser(response.data);
          this.isAuthenticatedSubject.next(true);
        } else {
          this.isAuthenticatedSubject.next(false);
        }
      }),
      catchError(error => {
        this.isAuthenticatedSubject.next(false);
        return of({
          success: false,
          message: 'Token validation failed'
        });
      })
    );
  }
  
  logout(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post(`${this.apiUrl}/logout`, {}, { headers })
      .pipe(
        tap(() => {
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