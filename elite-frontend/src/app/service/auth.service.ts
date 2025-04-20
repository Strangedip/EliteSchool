import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { username: username, password: password });
  }
  
  signup(userData:any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, userData);
  }

  validateToken(): Observable<any> {
    return this.http.get(`${this.apiUrl}/validate-token`);
  }
  
  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logout`, {});
  }
}