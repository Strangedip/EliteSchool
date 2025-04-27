import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { User, UserAuth } from '../models/user.model';
import { tap } from 'rxjs/operators';
import { CommonResponseDto } from '../models/common-response.model';
import { Role } from '../auth/enums/roles.enum';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;
  
  // User state management
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  private USER_DATA_KEY = 'user_data';

  constructor(private http: HttpClient) {
    // Initialize user data from localStorage if available
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
    return user !== null && user.role === 'ADMIN';
  }

  hasRole(role: Role): boolean {
    const user = this.getCurrentUser();
    return user !== null && user.role === role;
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

  createUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, userData);
  }

  updateUser(userId: string, userData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/update/${userId}`, userData);
  }
}
