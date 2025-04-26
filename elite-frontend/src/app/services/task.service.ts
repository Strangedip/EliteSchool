import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Task } from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private tasks: Task[] = [
    {
      id: 1,
      title: 'Complete Math Assignment',
      description: 'Finish Chapter 5 exercises',
      dueDate: '2023-11-10',
      priority: 'high',
      course: 'Mathematics',
      status: 'todo'
    },
    {
      id: 2,
      title: 'Physics Lab Report',
      description: 'Write up experiment results',
      dueDate: '2023-11-15',
      priority: 'medium',
      course: 'Physics',
      status: 'in-progress'
    },
    {
      id: 3,
      title: 'Literature Review',
      description: 'Review classic novels',
      dueDate: '2023-11-20',
      priority: 'low',
      course: 'English Literature',
      status: 'completed'
    }
  ];

  constructor() { }

  getTasks(): Observable<Task[]> {
    return of(this.tasks);
  }

  addTask(task: Task): Observable<Task> {
    const newTask = {
      ...task,
      id: this.tasks.length > 0 ? Math.max(...this.tasks.map(t => t.id)) + 1 : 1
    };
    this.tasks.push(newTask);
    return of(newTask);
  }

  updateTaskStatus(taskId: number, status: 'todo' | 'in-progress' | 'completed'): Observable<Task | undefined> {
    const task = this.tasks.find(t => t.id === taskId);
    if (task) {
      task.status = status;
      return of(task);
    }
    return of(undefined);
  }
} 