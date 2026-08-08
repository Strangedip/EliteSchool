import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Task, TaskSubmission, TaskTemplate } from '../models/task.model';
import { environment } from '../../../environments/environment';
import { CommonResponseDto } from '../models/common-response.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private tasksUrl = `${environment.apiUrl}/tasks`;
  private submissionsUrl = `${environment.apiUrl}/task-submissions`;
  private templatesUrl = `${environment.apiUrl}/task-templates`;

  constructor(private http: HttpClient) { }

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

  createTaskFromTemplate(templateId: string): Observable<Task> {
    return this.http.post<CommonResponseDto<Task>>(`${this.tasksUrl}/from-template/${templateId}`, null)
      .pipe(map(response => response.data as Task));
  }

  updateTask(taskId: string, task: Partial<Task>): Observable<Task> {
    return this.http.put<CommonResponseDto<Task>>(`${this.tasksUrl}/${taskId}`, task)
      .pipe(map(response => response.data as Task));
  }

  deleteTask(taskId: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.tasksUrl}/${taskId}`)
      .pipe(map(response => response.data));
  }

  getTaskTemplates(): Observable<TaskTemplate[]> {
    return this.http.get<CommonResponseDto<TaskTemplate[]>>(this.templatesUrl)
      .pipe(map(response => response.data ?? []));
  }

  getTaskTemplateById(templateId: string): Observable<TaskTemplate> {
    return this.http.get<CommonResponseDto<TaskTemplate>>(`${this.templatesUrl}/${templateId}`)
      .pipe(map(response => response.data as TaskTemplate));
  }

  createTaskTemplate(template: Partial<TaskTemplate>): Observable<TaskTemplate> {
    return this.http.post<CommonResponseDto<TaskTemplate>>(this.templatesUrl, template)
      .pipe(map(response => response.data as TaskTemplate));
  }

  updateTaskTemplate(templateId: string, template: Partial<TaskTemplate>): Observable<TaskTemplate> {
    return this.http.put<CommonResponseDto<TaskTemplate>>(`${this.templatesUrl}/${templateId}`, template)
      .pipe(map(response => response.data as TaskTemplate));
  }

  deleteTaskTemplate(templateId: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.templatesUrl}/${templateId}`)
      .pipe(map(response => response.data));
  }

  submitTask(submission: TaskSubmission): Observable<TaskSubmission> {
    return this.http.post<CommonResponseDto<TaskSubmission>>(`${this.submissionsUrl}`, submission)
      .pipe(map(response => response.data as TaskSubmission));
  }

  getSubmissionsByStudent(studentId: string): Observable<TaskSubmission[]> {
    return this.http.get<CommonResponseDto<TaskSubmission[]>>(`${this.submissionsUrl}/student/${studentId}`)
      .pipe(map(response => response.data ?? []));
  }

  getSubmissionsByStatus(status: string): Observable<TaskSubmission[]> {
    return this.http.get<CommonResponseDto<TaskSubmission[]>>(`${this.submissionsUrl}/status/${status}`)
      .pipe(map(response => response.data ?? []));
  }

  verifyTask(submissionId: string, approved: boolean, feedback: string): Observable<TaskSubmission> {
    const params = {
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
