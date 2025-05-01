import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
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
    return this.http.get<CommonResponseDto<User>>(`${this.apiUrl}/profile`).pipe(
      tap(response => {
        if (response.success && response.data) {
          this.setCurrentUser(response.data);
        }
      })
    );
  }

  updateUserProfile(userData: Partial<User>): Observable<CommonResponseDto<User>> {
    return this.http.put<CommonResponseDto<User>>(`${this.apiUrl}/profile/update`, userData).pipe(
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
    return this.http.put<CommonResponseDto<User>>(`${this.apiUrl}/update/${userId}`, userData);
  }
} 