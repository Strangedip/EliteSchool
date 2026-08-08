import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction } from '../models/wallet.model';
import { CommonResponseDto } from '../models/common-response.model';
import { ContributionNomination, NominationStatus } from '../models/nomination.model';

export interface PurchaseResponse {
  success: boolean;
  message: string;
  remainingBalance: number;
}

export interface WalletBalanceEntry {
  studentId: string;
  balance: number;
}

@Injectable({ providedIn: 'root' })
export class WalletService {
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

  purchaseItem(studentId: string, itemId: string): Observable<PurchaseResponse> {
    return this.http.post<CommonResponseDto<PurchaseResponse>>(`${this.apiUrl}/${studentId}/purchase/${itemId}`, null)
      .pipe(map(response => response.data ?? { success: false, message: 'No data returned', remainingBalance: 0 }));
  }

  getLeaderboard(limit: number = 10): Observable<WalletBalanceEntry[]> {
    return this.http.get<CommonResponseDto<WalletBalanceEntry[]>>(`${this.apiUrl}/leaderboard`, { params: { limit } })
      .pipe(map(response => response.data ?? []));
  }

  createNomination(body: ContributionNomination): Observable<ContributionNomination> {
    return this.http.post<CommonResponseDto<ContributionNomination>>(`${this.apiUrl}/nominations`, body)
      .pipe(map(r => r.data!));
  }

  listNominations(status?: NominationStatus | null): Observable<ContributionNomination[]> {
    const options = status ? { params: { status } } : {};
    return this.http.get<CommonResponseDto<ContributionNomination[]>>(`${this.apiUrl}/nominations`, options)
      .pipe(map(r => r.data ?? []));
  }

  approveNomination(id: string, points?: number, reviewNotes?: string): Observable<ContributionNomination> {
    const params: Record<string, string> = {};
    if (points != null) {
      params['points'] = String(points);
    }
    if (reviewNotes) {
      params['reviewNotes'] = reviewNotes;
    }
    return this.http.put<CommonResponseDto<ContributionNomination>>(
      `${this.apiUrl}/nominations/${id}/approve`,
      null,
      { params }
    ).pipe(map(r => r.data!));
  }

  rejectNomination(id: string, reviewNotes?: string): Observable<ContributionNomination> {
    const params: Record<string, string> = {};
    if (reviewNotes) {
      params['reviewNotes'] = reviewNotes;
    }
    return this.http.put<CommonResponseDto<ContributionNomination>>(
      `${this.apiUrl}/nominations/${id}/reject`,
      null,
      { params }
    ).pipe(map(r => r.data!));
  }
}
