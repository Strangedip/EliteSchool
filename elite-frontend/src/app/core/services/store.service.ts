import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StoreItem } from '../models/store-item.model';

@Injectable({
  providedIn: 'root'
})
export class StoreService {
  // API Gateway will route requests to the store-service
  private apiUrl = `${environment.apiUrl}/store`;

  constructor(private http: HttpClient) { }

  getAllItems(): Observable<StoreItem[]> {
    return this.http.get<StoreItem[]>(`${this.apiUrl}/items`);
  }

  getItemById(itemId: string): Observable<StoreItem> {
    return this.http.get<StoreItem>(`${this.apiUrl}/items/${itemId}`);
  }

  addItem(item: StoreItem): Observable<StoreItem> {
    return this.http.post<StoreItem>(`${this.apiUrl}/items`, item);
  }

  updateItem(itemId: string, item: StoreItem): Observable<StoreItem> {
    return this.http.put<StoreItem>(`${this.apiUrl}/items/${itemId}`, item);
  }

  deleteItem(itemId: string): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/items/${itemId}`);
  }

  purchaseItem(studentId: string, itemId: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/purchase/${studentId}/${itemId}`, {});
  }
} 