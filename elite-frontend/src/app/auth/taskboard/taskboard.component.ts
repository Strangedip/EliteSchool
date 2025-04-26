import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Task } from '../../models/task.model';
import { TaskService } from '../../services/task.service';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-taskboard',
  templateUrl: './taskboard.component.html',
  styleUrls: ['./taskboard.component.scss'],
  standalone: true,
  imports: [CommonModule, ButtonModule]
})
export class TaskboardComponent implements OnInit {
  todoTasks: Task[] = [];
  inProgressTasks: Task[] = [];
  completedTasks: Task[] = [];
  allTasks: Task[] = [];
  searchQuery: string = '';

  constructor(private taskService: TaskService) { }

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.taskService.getTasks().subscribe(tasks => {
      this.allTasks = tasks;
      this.filterTasks();
    });
  }

  filterTasks(): void {
    const filteredTasks = this.searchQuery 
      ? this.allTasks.filter(task => 
          task.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          task.description.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          task.course.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      : this.allTasks;
      
    this.todoTasks = filteredTasks.filter(task => task.status === 'todo');
    this.inProgressTasks = filteredTasks.filter(task => task.status === 'in-progress');
    this.completedTasks = filteredTasks.filter(task => task.status === 'completed');
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterTasks();
  }

  addTask(): void {
    // Will open a dialog or navigate to add task form
    console.log('Adding new task...');
  }

  getPriorityClass(priority: string): string {
    switch (priority.toLowerCase()) {
      case 'high':
        return 'priority-high';
      case 'medium':
        return 'priority-medium';
      case 'low':
        return 'priority-low';
      default:
        return 'priority-medium';
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
  }
}
