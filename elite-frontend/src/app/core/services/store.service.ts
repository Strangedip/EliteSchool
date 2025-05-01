import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StoreItem } from '../models/store-item.model';
import { CommonResponseDto } from '../models/common-response.model';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  // API Gateway will route requests to the store-service
  private apiUrl = `${environment.apiUrl}/store`;

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<StoreItem[]> {
    return this.http.get<CommonResponseDto<StoreItem[]>>(`${this.apiUrl}/items`)
      .pipe(map(response => response.data ?? []));
  }

  getItemById(itemId: string): Observable<StoreItem> {
    return this.http.get<CommonResponseDto<StoreItem>>(`${this.apiUrl}/items/${itemId}`)
      .pipe(map(response => response.data as StoreItem));
  }

  addItem(item: StoreItem): Observable<StoreItem> {
    return this.http.post<CommonResponseDto<StoreItem>>(`${this.apiUrl}/items`, item)
      .pipe(map(response => response.data as StoreItem));
  }

  updateItem(itemId: string, item: StoreItem): Observable<StoreItem> {
    return this.http.put<CommonResponseDto<StoreItem>>(`${this.apiUrl}/items/${itemId}`, item)
      .pipe(map(response => response.data as StoreItem));
  }

  deleteItem(itemId: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.apiUrl}/items/${itemId}`);
  }

  purchaseItem(studentId: string, itemId: string): Observable<any> {
    // Use the rewards service endpoint to spend points instead, which will internally call the store service
    return this.http.post<CommonResponseDto<any>>(`${environment.apiUrl}/rewards/spend/${studentId}/${itemId}`, {})
      .pipe(map(response => response.data));
  }
} 