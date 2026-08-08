import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { Select } from 'primeng/select';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';

import { Course } from '../../core/models/course.model';
import { CourseService } from '../../core/services/course.service';
import { UserService } from '../../core/services/user.service';

const GRADE_OPTIONS = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12'].map(g => ({ label: `Grade ${g}`, value: g }));

@Component({
    selector: 'app-courses',
    templateUrl: './courses.component.html',
    styleUrls: ['./courses.component.scss'],
    imports: [
    FormsModule,
    ButtonModule,
    DialogModule,
    InputTextModule,
    Textarea,
    Select,
    ConfirmDialogModule,
    ToastModule,
    CardModule,
    TagModule
],
    providers: [ConfirmationService, MessageService],
    changeDetection: ChangeDetectionStrategy.Eager
})
export class CoursesComponent implements OnInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  searchQuery = '';
  subjectFilter: string | null = null;
  gradeFilter: string | null = null;
  currentUserRole = '';

  subjectOptions: { label: string; value: string }[] = [];
  gradeOptions = GRADE_OPTIONS;

  courseDialogVisible = false;
  editMode = false;
  selectedCourse: Course | null = null;

  constructor(
    private courseService: CourseService,
    private userService: UserService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.currentUserRole = this.userService.getCurrentUser()?.role || '';
    this.loadCourses();
  }

  isManager(): boolean {
    const role = this.currentUserRole?.toUpperCase() || '';
    return ['ADMIN', 'MANAGEMENT'].includes(role);
  }

  loadCourses(): void {
    this.courseService.getCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.subjectOptions = Array.from(new Set(courses.map(c => c.subject)))
          .sort()
          .map(s => ({ label: s, value: s }));
        this.applyFilters();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to load courses: ${error.message}` });
      }
    });
  }

  applyFilters(): void {
    const query = this.searchQuery.toLowerCase();
    this.filteredCourses = this.courses.filter(course => {
      const matchesQuery = !query ||
        course.name.toLowerCase().includes(query) ||
        (course.description || '').toLowerCase().includes(query) ||
        (course.courseCode || '').toLowerCase().includes(query);
      const matchesSubject = !this.subjectFilter || course.subject === this.subjectFilter;
      const matchesGrade = !this.gradeFilter || course.grade === this.gradeFilter;
      return matchesQuery && matchesSubject && matchesGrade;
    });
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  addCourse(): void {
    this.selectedCourse = { name: '', subject: '', grade: '', description: '', active: true };
    this.editMode = false;
    this.courseDialogVisible = true;
  }

  editCourse(course: Course): void {
    this.selectedCourse = { ...course };
    this.editMode = true;
    this.courseDialogVisible = true;
  }

  saveCourse(): void {
    if (!this.selectedCourse) return;

    const operation = this.editMode && this.selectedCourse.id
      ? this.courseService.updateCourse(this.selectedCourse.id, this.selectedCourse)
      : this.courseService.createCourse(this.selectedCourse);

    operation.subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: `Course ${this.editMode ? 'updated' : 'created'} successfully` });
        this.courseDialogVisible = false;
        this.loadCourses();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to ${this.editMode ? 'update' : 'create'} course: ${error.error?.message || error.message}` });
      }
    });
  }

  toggleActive(course: Course): void {
    if (!course.id) return;
    this.courseService.setActive(course.id, !course.active).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: `Course ${course.active ? 'deactivated' : 'activated'}` });
        this.loadCourses();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to update status: ${error.message}` });
      }
    });
  }

  deleteCourse(course: Course): void {
    if (!course.id) return;
    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${course.name}"?`,
      accept: () => {
        this.courseService.deleteCourse(course.id!).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'Course deleted successfully' });
            this.loadCourses();
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to delete course: ${error.message}` });
          }
        });
      }
    });
  }
}
