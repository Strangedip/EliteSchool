import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  createTask(taskData: any): Observable<any> {
    return this.http.post(this.apiUrl, taskData);
  }

  getTasks(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  completeTask(taskId: number, completedBy: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${taskId}/complete`, null, { params: { completedBy } });
  }
}