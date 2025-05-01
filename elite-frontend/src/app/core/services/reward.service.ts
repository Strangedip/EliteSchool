import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RewardTransaction } from '../models/reward.model';
import { CommonResponseDto } from '../models/common-response.model';

/**
 * Response from the redemption endpoint
 */
export interface RedemptionResponse {
  success: boolean;
  message: string;
  remainingBalance: number;
}

@Injectable({
  providedIn: 'root'
})
export class RewardService {
  // API Gateway will route requests to the reward-service
  private apiUrl = `${environment.apiUrl}/rewards`;

  constructor(private http: HttpClient) { }

  getWalletBalance(studentId: string): Observable<number> {
    return this.http.get<CommonResponseDto<number>>(`${this.apiUrl}/wallet/${studentId}`)
      .pipe(map(response => response.data ?? 0));
  }

  getTransactionHistory(studentId: string): Observable<RewardTransaction[]> {
    return this.http.get<CommonResponseDto<RewardTransaction[]>>(`${this.apiUrl}/transactions/${studentId}`)
      .pipe(map(response => response.data ?? []));
  }

  earnPoints(studentId: string, points: number, description: string): Observable<number> {
    return this.http.post<CommonResponseDto<number>>(`${this.apiUrl}/earn/${studentId}`, null, {
      params: { points: points.toString(), description }
    }).pipe(map(response => response.data ?? 0));
  }

  /**
   * Spend points to redeem a store item
   * @param studentId The student's ID
   * @param itemId The item ID to redeem
   * @returns Observable with detailed redemption response
   */
  spendPoints(studentId: string, itemId: string): Observable<RedemptionResponse> {
    return this.http.post<CommonResponseDto<RedemptionResponse>>(`${this.apiUrl}/spend/${studentId}/${itemId}`, null)
      .pipe(map(response => response.data ?? { success: false, message: 'No data returned', remainingBalance: 0 }));
  }
} 