import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Course } from '../models/course.model';
import { CommonResponseDto } from '../models/common-response.model';

@Injectable({ providedIn: 'root' })
export class CourseService {
  private apiUrl = `${environment.apiUrl}/courses`;

  constructor(private http: HttpClient) { }

  getCourses(): Observable<Course[]> {
    return this.http.get<CommonResponseDto<Course[]>>(this.apiUrl)
      .pipe(map(response => response.data ?? []));
  }

  createCourse(course: Course): Observable<Course> {
    return this.http.post<CommonResponseDto<Course>>(this.apiUrl, course)
      .pipe(map(response => response.data as Course));
  }

  updateCourse(id: string, course: Course): Observable<Course> {
    return this.http.put<CommonResponseDto<Course>>(`${this.apiUrl}/${id}`, course)
      .pipe(map(response => response.data as Course));
  }

  setActive(id: string, active: boolean): Observable<Course> {
    return this.http.put<CommonResponseDto<Course>>(`${this.apiUrl}/${id}/active`, null, { params: { active } })
      .pipe(map(response => response.data as Course));
  }

  deleteCourse(id: string): Observable<any> {
    return this.http.delete<CommonResponseDto<void>>(`${this.apiUrl}/${id}`);
  }
}
