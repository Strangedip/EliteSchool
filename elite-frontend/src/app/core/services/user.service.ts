import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { User } from '../models/user.model';
import { CommonResponseDto } from '../models/common-response.model';

/**
 * User roles enum
 */
export enum Role {
  ADMIN = 'ADMIN',
  MANAGEMENT = 'MANAGEMENT',
  FACULTY = 'FACULTY',
  STUDENT = 'STUDENT'
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;
  private authUrl = `${environment.apiUrl}/auth`;
  
  // User state management
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private USER_DATA_KEY = 'user_data';

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    const userData = localStorage.getItem(this.USER_DATA_KEY);
    if (userData) {
      try {
        const parsedUser = JSON.parse(userData) as User;
        this.currentUserSubject.next(parsedUser);
      } catch (error) {
        console.error('Error parsing user data from storage', error);
        localStorage.removeItem(this.USER_DATA_KEY);
      }
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('Authorization') || localStorage.getItem('token');
    let headers = new HttpHeaders();
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }

  setCurrentUser(user: User): void {
    localStorage.setItem(this.USER_DATA_KEY, JSON.stringify(user));
    this.currentUserSubject.next(user);
  }

  clearCurrentUser(): void {
    localStorage.removeItem(this.USER_DATA_KEY);
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isAdmin(): boolean {
    const user = this.getCurrentUser();
    return user !== null && user.role === Role.ADMIN;
  }

  hasRole(role: Role | string): boolean {
    const user = this.getCurrentUser();
    if (!user) return false;
    
    const roleStr = typeof role === 'string' ? role : role;
    return user.role === roleStr;
  }

  getUserProfile(): Observable<CommonResponseDto<User>> {
    // Use current user from storage if available
    const currentUser = this.getCurrentUser();
    if (currentUser) {
      return of({ success: true, message: 'User loaded from storage', data: currentUser });
    }
    
    // Otherwise, get from the profile endpoint
    const headers = this.getAuthHeaders();
    if (!headers.has('Authorization')) {
      return of({ success: false, message: 'No authentication token available' });
    }
    
    return this.http.get<CommonResponseDto<User>>(
      `${this.authUrl}/profile`, 
      { headers }
    ).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setCurrentUser(response.data);
        }
      }),
      catchError(error => {
        console.error('Error getting user profile:', error);
        
        // Fallback to token validation if profile endpoint fails
        return this.http.get<CommonResponseDto<User>>(
          `${this.authUrl}/validate-token`, 
          { headers }
        ).pipe(
          tap(response => {
            if (response.success && response.data) {
              this.setCurrentUser(response.data);
            }
          }),
          catchError(_ => {
            return of({ success: false, message: 'Failed to get user profile' });
          })
        );
      })
    );
  }

  updateUserProfile(userData: Partial<User>): Observable<CommonResponseDto<User>> {
    return this.http.put<CommonResponseDto<User>>(`${this.apiUrl}/${userData.id || userData.eliteId}`, userData).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setCurrentUser(response.data);
        }
      })
    );
  }

  createUser(userData: any): Observable<CommonResponseDto<User>> {
    return this.http.post<CommonResponseDto<User>>(`${this.apiUrl}/create`, userData);
  }

  updateUser(userId: string, userData: Partial<User>): Observable<CommonResponseDto<User>> {
    return this.http.put<CommonResponseDto<User>>(`${this.apiUrl}/${userId}`, userData);
  }
  
  getUserById(userId: string): Observable<CommonResponseDto<User>> {
    return this.http.get<CommonResponseDto<User>>(`${this.apiUrl}/${userId}`);
  }
} 