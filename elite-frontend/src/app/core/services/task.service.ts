import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Task, TaskSubmission } from '../models/task.model';
import { environment } from '../../../environments/environment';
import { CommonResponseDto } from '../models/common-response.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  // API Gateway will route requests to the appropriate microservice
  private apiUrl = `${environment.apiUrl}`;
  private tasksUrl = `${environment.apiUrl}/tasks`;
  private submissionsUrl = `${environment.apiUrl}/task-submissions`;

  constructor(private http: HttpClient) { }

  // Task endpoints
  getTasks(): Observable<Task[]> {
    return this.http.get<CommonResponseDto<Task[]>>(`${this.tasksUrl}/all`)
      .pipe(map(response => response.data ?? []));
  }

  getOpenTasks(): Observable<Task[]> {
    return this.http.get<CommonResponseDto<Task[]>>(`${this.tasksUrl}/status/OPEN`)
      .pipe(map(response => response.data ?? []));
  }

  getTaskById(taskId: string): Observable<Task> {
    return this.http.get<CommonResponseDto<Task>>(`${this.tasksUrl}/${taskId}`)
      .pipe(map(response => response.data as Task));
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<CommonResponseDto<Task>>(`${this.tasksUrl}/create`, task)
      .pipe(map(response => response.data as Task));
  }

  updateTask(taskId: string, task: Partial<Task>): Observable<Task> {
    return this.http.put<CommonResponseDto<Task>>(`${this.tasksUrl}/${taskId}`, task)
      .pipe(map(response => response.data as Task));
  }

  closeTask(taskId: string): Observable<Task> {
    return this.http.put<CommonResponseDto<Task>>(`${this.tasksUrl}/${taskId}/close`, {})
      .pipe(map(response => response.data as Task));
  }

  deleteTask(taskId: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.tasksUrl}/${taskId}`)
      .pipe(map(response => response.data));
  }

  // Task submission endpoints
  submitTask(submission: TaskSubmission): Observable<TaskSubmission> {
    return this.http.post<CommonResponseDto<TaskSubmission>>(`${this.submissionsUrl}`, submission)
      .pipe(map(response => response.data as TaskSubmission));
  }

  getSubmissionsByTask(taskId: string): Observable<TaskSubmission[]> {
    return this.http.get<CommonResponseDto<TaskSubmission[]>>(`${this.submissionsUrl}/task/${taskId}`)
      .pipe(map(response => response.data ?? []));
  }

  getSubmissionsByStudent(studentId: string): Observable<TaskSubmission[]> {
    return this.http.get<CommonResponseDto<TaskSubmission[]>>(`${this.submissionsUrl}/student/${studentId}`)
      .pipe(map(response => response.data ?? []));
  }

  getSubmissionsByStatus(status: string): Observable<TaskSubmission[]> {
    return this.http.get<CommonResponseDto<TaskSubmission[]>>(`${this.submissionsUrl}/status/${status}`)
      .pipe(map(response => response.data ?? []));
  }

  verifyTask(submissionId: string, verifierId: string, approved: boolean, feedback: string): Observable<TaskSubmission> {
    const params = {
      verifierId,
      approved: approved.toString(),
      feedback: feedback || ''
    };
    
    return this.http.put<CommonResponseDto<TaskSubmission>>(
      `${this.submissionsUrl}/${submissionId}/verify`,
      null,
      { params }
    ).pipe(map(response => response.data as TaskSubmission));
  }
} 