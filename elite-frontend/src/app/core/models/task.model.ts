export interface Task {
  id: string;
  title: string;
  description: string;
  taskType: 'SINGLE' | 'MULTIPLE';
  minLevel: number;
  rewardPoints: number;
  createdBy: string;
  status: 'OPEN' | 'SUBMITTED' | 'COMPLETED' | 'REJECTED' | 'CLOSED';
  completedBy?: string;
  completedAt?: string;
  createdAt: string;
  priority?: 'high' | 'medium' | 'low';
}

export interface TaskSubmission {
  id?: string;
  taskId: string;
  studentId: string;
  submissionDetails: string;
  evidence: string;
  status?: 'SUBMITTED' | 'COMPLETED' | 'REJECTED';
  feedbackNotes?: string;
  verifiedBy?: string;
  submittedAt?: string;
  updatedAt?: string;
  verifiedAt?: string;
  
  taskTitle?: string;
  rewardPoints?: number;
} 