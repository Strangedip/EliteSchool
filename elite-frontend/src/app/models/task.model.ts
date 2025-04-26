export interface Task {
  id: number;
  title: string;
  description: string;
  dueDate: string;
  priority: 'high' | 'medium' | 'low';
  course: string;
  status: 'todo' | 'in-progress' | 'completed';
} 