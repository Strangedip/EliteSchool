import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  SupportMessage,
  SupportTicket,
  SupportTicketStatus,
  UpdateSupportStatusRequest
} from '../models/support.model';
import { CommonResponseDto } from '../models/common-response.model';

@Injectable({ providedIn: 'root' })
export class SupportService {
  private apiUrl = `${environment.apiUrl}/support`;

  constructor(private http: HttpClient) { }

  createTicket(ticket: SupportTicket): Observable<SupportTicket> {
    return this.http.post<CommonResponseDto<SupportTicket>>(`${this.apiUrl}/tickets`, ticket)
      .pipe(map(response => response.data as SupportTicket));
  }

  getMyTickets(): Observable<SupportTicket[]> {
    return this.http.get<CommonResponseDto<SupportTicket[]>>(`${this.apiUrl}/tickets/mine`)
      .pipe(map(response => response.data ?? []));
  }

  getAllTickets(status?: SupportTicketStatus | null): Observable<SupportTicket[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<CommonResponseDto<SupportTicket[]>>(`${this.apiUrl}/tickets`, { params })
      .pipe(map(response => response.data ?? []));
  }

  getTicket(id: string): Observable<SupportTicket> {
    return this.http.get<CommonResponseDto<SupportTicket>>(`${this.apiUrl}/tickets/${id}`)
      .pipe(map(response => response.data as SupportTicket));
  }

  addMessage(ticketId: string, body: string): Observable<SupportMessage> {
    return this.http.post<CommonResponseDto<SupportMessage>>(
      `${this.apiUrl}/tickets/${ticketId}/messages`,
      { body }
    ).pipe(map(response => response.data as SupportMessage));
  }

  updateStatus(ticketId: string, request: UpdateSupportStatusRequest): Observable<SupportTicket> {
    return this.http.put<CommonResponseDto<SupportTicket>>(
      `${this.apiUrl}/tickets/${ticketId}/status`,
      request
    ).pipe(map(response => response.data as SupportTicket));
  }
}
