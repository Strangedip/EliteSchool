import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError, map } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { UserService } from './user.service';
import { CommonResponseDto } from '../models/common-response.model';
import { LoginResponseDto, User } from '../models/user.model';

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
  
  constructor(
    private http: HttpClient, 
    private router: Router,
    private userService: UserService
  ) {
    this.checkAuthState();
  }

  private checkAuthState(): void {
    const token = this.getToken();
    this.isAuthenticatedSubject.next(!!token);
  }

  private getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem('token', token); // Legacy support
    this.isAuthenticatedSubject.next(true);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY) || localStorage.getItem('token');
  }

  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
  }

  login(username: string, password: string): Observable<CommonResponseDto<LoginResponseDto>> {
    return this.http.post<CommonResponseDto<any>>(`${this.apiUrl}/login`, { username, password }).pipe(
      map(response => {
        if (response.success && response.data) {
          const { token, user } = response.data;
          this.saveToken(token);
          if (user) this.userService.setCurrentUser(user);
          return { success: response.success, message: response.message, data: { token, user } };
        }
        return response;
      })
    );
  }
  
  signup(userData: UserRegistrationData): Observable<CommonResponseDto<LoginResponseDto>> {
    userData.role = userData.role.toUpperCase();
    userData.gender = userData.gender.toUpperCase();
    
    return this.http.post<CommonResponseDto<LoginResponseDto>>(`${this.apiUrl}/signup`, userData).pipe(
      tap((response: CommonResponseDto<LoginResponseDto>) => {
        if (response.success && response.data) {
          this.saveToken(response.data.token);
          if (response.data.user) this.userService.setCurrentUser(response.data.user);
        }
      })
    );
  }

  validateToken(): Observable<CommonResponseDto<User>> {
    const headers = this.getAuthHeaders();
    if (!headers.has('Authorization')) {
      return of({ success: false, message: 'No token available' });
    }
    
    return this.http.get<CommonResponseDto<User>>(`${this.apiUrl}/validate-token`, { headers }).pipe(
      tap((response: CommonResponseDto<User>) => {
        if (response.success && response.data) {
          this.userService.setCurrentUser(response.data);
          this.isAuthenticatedSubject.next(true);
        } else {
          this.isAuthenticatedSubject.next(false);
        }
      }),
      catchError(() => {
        this.isAuthenticatedSubject.next(false);
        return of({ success: false, message: 'Token validation failed' });
      })
    );
  }
  
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {}, { headers: this.getAuthHeaders() }).pipe(
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
    this.router.navigate(['/login']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  // ==================== PASSWORD RESET METHODS ====================

  /**
   * Request password reset
   * Sends email with reset link if email exists
   */
  forgotPassword(email: string): Observable<CommonResponseDto<any>> {
    return this.http.post<CommonResponseDto<any>>(`${this.apiUrl}/forgot-password`, { email });
  }

  /**
   * Reset password using token
   * Validates token and updates password
   */
  resetPassword(token: string, newPassword: string): Observable<CommonResponseDto<any>> {
    return this.http.post<CommonResponseDto<any>>(`${this.apiUrl}/reset-password`, { 
      token, 
      newPassword 
    });
  }

  /**
   * Validate reset token
   * Checks if token is valid and not expired
   */
  validateResetToken(token: string): Observable<CommonResponseDto<{ valid: boolean, message: string }>> {
    return this.http.get<CommonResponseDto<{ valid: boolean, message: string }>>(
      `${this.apiUrl}/validate-reset-token/${token}`
    );
  }
}
