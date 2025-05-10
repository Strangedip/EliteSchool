import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction } from '../models/wallet.model';
import { CommonResponseDto } from '../models/common-response.model';

/**
 * Response from the purchase endpoint
 */
export interface PurchaseResponse {
  success: boolean;
  message: string;
  remainingBalance: number;
}

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  // API Gateway will route requests to the wallet-service
  private apiUrl = `${environment.apiUrl}/wallet`;

  constructor(private http: HttpClient) { }

  getWalletBalance(studentId: string): Observable<number> {
    return this.http.get<CommonResponseDto<number>>(`${this.apiUrl}/${studentId}/balance`)
      .pipe(map(response => response.data ?? 0));
  }

  getTransactionHistory(studentId: string): Observable<Transaction[]> {
    return this.http.get<CommonResponseDto<Transaction[]>>(`${this.apiUrl}/${studentId}/transactions`)
      .pipe(map(response => response.data ?? []));
  }

  creditPoints(studentId: string, points: number, description: string): Observable<number> {
    return this.http.post<CommonResponseDto<number>>(`${this.apiUrl}/${studentId}/credit`, null, {
      params: { points: points.toString(), description }
    }).pipe(map(response => response.data ?? 0));
  }

  debitPoints(studentId: string, points: number, description: string): Observable<number> {
    return this.http.post<CommonResponseDto<number>>(`${this.apiUrl}/${studentId}/debit`, null, {
      params: { points: points.toString(), description }
    }).pipe(map(response => response.data ?? 0));
  }

  /**
   * Purchase an item from the store
   * @param studentId The student's ID
   * @param itemId The item ID to purchase
   * @returns Observable with detailed purchase response
   */
  purchaseItem(studentId: string, itemId: string): Observable<PurchaseResponse> {
    return this.http.post<CommonResponseDto<PurchaseResponse>>(`${this.apiUrl}/${studentId}/purchase/${itemId}`, null)
      .pipe(map(response => response.data ?? { success: false, message: 'No data returned', remainingBalance: 0 }));
  }
} 