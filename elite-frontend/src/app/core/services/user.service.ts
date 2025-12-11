import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { User } from '../models/user.model';
import { CommonResponseDto } from '../models/common-response.model';

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
        this.currentUserSubject.next(JSON.parse(userData) as User);
      } catch {
        localStorage.removeItem(this.USER_DATA_KEY);
      }
    }
  }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('Authorization') || localStorage.getItem('token');
    return token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : new HttpHeaders();
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
    return this.getCurrentUser()?.role === Role.ADMIN;
  }

  hasRole(role: Role | string): boolean {
    return this.getCurrentUser()?.role === role;
  }

  getUserProfile(): Observable<CommonResponseDto<User>> {
    const currentUser = this.getCurrentUser();
    if (currentUser) {
      return of({ success: true, message: 'User loaded', data: currentUser });
    }
    
    const headers = this.getAuthHeaders();
    if (!headers.has('Authorization')) {
      return of({ success: false, message: 'No token available' });
    }
    
    return this.http.get<CommonResponseDto<User>>(`${this.authUrl}/profile`, { headers }).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setCurrentUser(response.data);
        }
      }),
      catchError(() => {
        // Fallback to token validation
        return this.http.get<CommonResponseDto<User>>(`${this.authUrl}/validate-token`, { headers }).pipe(
          tap(response => {
            if (response.success && response.data) {
              this.setCurrentUser(response.data);
            }
          }),
          catchError(() => of({ success: false, message: 'Failed to get profile' }))
        );
      })
    );
  }

  updateUserProfile(userData: Partial<User>): Observable<CommonResponseDto<User>> {
    return this.http.put<CommonResponseDto<User>>(`${this.apiUrl}/${userData.eliteId}`, userData).pipe(
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
