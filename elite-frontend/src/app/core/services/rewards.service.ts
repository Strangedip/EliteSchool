import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RewardItem } from '../models/reward-item.model';
import { CommonResponseDto } from '../models/common-response.model';
import { PointsService } from './points.service';

@Injectable({ providedIn: 'root' })
export class RewardsService {
  private apiUrl = `${environment.apiUrl}/rewards`;

  constructor(
    private http: HttpClient,
    private pointsService: PointsService
  ) { }

  getAllItems(): Observable<RewardItem[]> {
    return this.http.get<CommonResponseDto<RewardItem[]>>(`${this.apiUrl}/items`)
      .pipe(map(response => response.data ?? []));
  }

  addItem(item: RewardItem): Observable<RewardItem> {
    return this.http.post<CommonResponseDto<RewardItem>>(`${this.apiUrl}/items`, item)
      .pipe(map(response => response.data as RewardItem));
  }

  updateItem(itemId: string, item: RewardItem): Observable<RewardItem> {
    return this.http.put<CommonResponseDto<RewardItem>>(`${this.apiUrl}/items/${itemId}`, item)
      .pipe(map(response => response.data as RewardItem));
  }

  deleteItem(itemId: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.apiUrl}/items/${itemId}`);
  }

  purchaseItem(studentId: string, itemId: string): Observable<any> {
    return this.pointsService.purchaseItem(studentId, itemId);
  }

  getPurchasesForStudent(studentId: string): Observable<import('../models/reward-item.model').RewardClaim[]> {
    return this.http.get<CommonResponseDto<import('../models/reward-item.model').RewardClaim[]>>(
      `${this.apiUrl}/purchases/student/${studentId}`
    ).pipe(map(response => response.data ?? []));
  }

  getAllPurchases(): Observable<import('../models/reward-item.model').RewardClaim[]> {
    return this.http.get<CommonResponseDto<import('../models/reward-item.model').RewardClaim[]>>(
      `${this.apiUrl}/purchases`
    ).pipe(map(response => response.data ?? []));
  }
}
