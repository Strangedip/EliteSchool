import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RewardTransaction } from '../models/reward.model';

@Injectable({
  providedIn: 'root'
})
export class RewardService {
  // API Gateway will route requests to the reward-service
  private apiUrl = `${environment.apiUrl}/rewards`;

  constructor(private http: HttpClient) { }

  getWalletBalance(studentId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/wallet/${studentId}`);
  }

  getTransactionHistory(studentId: string): Observable<RewardTransaction[]> {
    return this.http.get<RewardTransaction[]>(`${this.apiUrl}/transactions/${studentId}`);
  }

  earnPoints(studentId: string, points: number, description: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/earn/${studentId}`, null, {
      params: { points: points.toString(), description }
    });
  }

  spendPoints(studentId: string, itemId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/spend/${studentId}/${itemId}`, null);
  }
} 