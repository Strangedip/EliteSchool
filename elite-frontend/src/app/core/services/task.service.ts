import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskSubmission } from '../models/task.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  // API Gateway will route requests to the appropriate microservice
  private apiUrl = `${environment.apiUrl}`;

  constructor(private http: HttpClient) { }

  // Task endpoints
  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks`);
  }

  getOpenTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/tasks/status/OPEN`);
  }

  getTaskById(taskId: string): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/tasks/${taskId}`);
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(`${this.apiUrl}/tasks`, task);
  }

  updateTask(taskId: string, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/tasks/${taskId}`, task);
  }

  closeTask(taskId: string): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/tasks/${taskId}/close`, {});
  }

  // Task submission endpoints
  submitTask(submission: TaskSubmission): Observable<TaskSubmission> {
    return this.http.post<TaskSubmission>(`${this.apiUrl}/task-submissions`, submission);
  }

  getSubmissionsByTask(taskId: string): Observable<TaskSubmission[]> {
    return this.http.get<TaskSubmission[]>(`${this.apiUrl}/task-submissions/task/${taskId}`);
  }

  getSubmissionsByStudent(studentId: string): Observable<TaskSubmission[]> {
    return this.http.get<TaskSubmission[]>(`${this.apiUrl}/task-submissions/student/${studentId}`);
  }

  getSubmissionsByStatus(status: string): Observable<TaskSubmission[]> {
    return this.http.get<TaskSubmission[]>(`${this.apiUrl}/task-submissions/status/${status}`);
  }

  verifyTask(submissionId: string, verifierId: string, approved: boolean, feedback: string): Observable<TaskSubmission> {
    const params = {
      verifierId,
      approved: approved.toString(),
      feedback: feedback || ''
    };
    
    return this.http.put<TaskSubmission>(
      `${this.apiUrl}/task-submissions/${submissionId}/verify`,
      null,
      { params }
    );
  }
} 