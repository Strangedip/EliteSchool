import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TaskService } from '../../services/task.service';
import { Task } from '../../models/task.model';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class TaskListComponent implements OnInit {
  tasks: Task[] = [];
  filteredTasks: Task[] = [];
  courses: string[] = ['Mathematics', 'Physics', 'English Literature'];
  
  // Filter values
  statusFilter: string = 'all';
  priorityFilter: string = 'all';
  courseFilter: string = 'all';

  constructor(
    private taskService: TaskService
  ) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe(tasks => {
      this.tasks = tasks;
      this.applyFilters();
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      // Status filter
      if (this.statusFilter !== 'all' && task.status !== this.statusFilter) {
        return false;
      }
      
      // Priority filter
      if (this.priorityFilter !== 'all' && task.priority !== this.priorityFilter) {
        return false;
      }
      
      // Course filter
      if (this.courseFilter !== 'all' && task.course !== this.courseFilter) {
        return false;
      }
      
      return true;
    });
  }

  onStatusFilterChange(event: Event): void {
    this.statusFilter = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onPriorityFilterChange(event: Event): void {
    this.priorityFilter = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  onCourseFilterChange(event: Event): void {
    this.courseFilter = (event.target as HTMLSelectElement).value;
    this.applyFilters();
  }

  updateTaskStatus(task: Task, event: Event): void {
    const newStatus = (event.target as HTMLSelectElement).value as 'todo' | 'in-progress' | 'completed';
    this.taskService.updateTaskStatus(task.id, newStatus).subscribe(updatedTask => {
      if (updatedTask) {
        // Update the local task object
        const index = this.tasks.findIndex(t => t.id === task.id);
        if (index !== -1) {
          this.tasks[index] = updatedTask;
          this.applyFilters();
        }
      }
    });
  }

  getTaskPriorityClass(priority: string): string {
    return `priority-${priority}`;
  }
} 