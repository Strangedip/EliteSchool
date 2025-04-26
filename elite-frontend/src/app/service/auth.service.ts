import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError, shareReplay } from 'rxjs';
import { tap, catchError, debounceTime } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private TOKEN_KEY = 'auth_token';
  private tokenValidationInProgress: Observable<any> | null = null;
  private lastValidationTime = 0;
  private validationThrottleTime = 15000; // 15 seconds
  
  constructor(private http: HttpClient, private router: Router) {
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

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { username: username, password: password })
      .pipe(
        tap((response: any) => {
          if (response.success && response.data && response.data.token) {
            this.saveToken(response.data.token);
          }
        })
      );
  }
  
  signup(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData)
      .pipe(
        tap((response: any) => {
          if (response.success && response.data && response.data.token) {
            this.saveToken(response.data.token);
          }
        })
      );
  }

  validateToken(): Observable<any> {
    // Use the token from localStorage in the Authorization header
    const headers = this.getAuthHeaders();
    if (!headers.has('Authorization')) {
      return of({ success: false });
    }
    
    // Check if we need to throttle validation requests
    const now = Date.now();
    if (this.tokenValidationInProgress) {
      return this.tokenValidationInProgress;
    }
    
    if (now - this.lastValidationTime < this.validationThrottleTime) {
      // Return the current auth state if we've validated recently
      return of({ success: this.isAuthenticatedSubject.value });
    }
    
    // Create new validation request and share it
    this.lastValidationTime = now;
    this.tokenValidationInProgress = this.http.get(`${this.apiUrl}/validate-token`, { headers })
      .pipe(
        tap((response: any) => {
          if (!response.success) {
            this.clearToken();
          }
        }),
        catchError((error) => {
          this.clearToken();
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
        }),
        catchError((error) => {
          this.clearToken();
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