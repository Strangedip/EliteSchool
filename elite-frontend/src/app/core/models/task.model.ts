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
  evidenceRequired?: boolean;
  minNotesLength?: number;
  rubricChecklist?: string[];
  submissionId?: string;
  submissionStatus?: string;
  feedbackNotes?: string;
  submissionDetails?: string;
  evidence?: string;
  rubricChecked?: string[];
}

export interface TaskTemplate {
  id: string;
  title: string;
  description: string;
  taskType: 'SINGLE' | 'MULTIPLE';
  minLevel: number;
  rewardPoints: number;
  createdBy: string;
  createdAt: string;
  evidenceRequired?: boolean;
  minNotesLength?: number;
  rubricChecklist?: string[];
}

export type SubmissionStatus = 'SUBMITTED' | 'COMPLETED' | 'REJECTED';

export interface TaskSubmission {
  id?: string;
  taskId: string;
  studentId: string;
  submissionDetails: string;
  evidence: string;
  rubricChecked?: string[];
  status?: SubmissionStatus;
  feedbackNotes?: string;
  verifiedBy?: string;
  submittedAt?: string;
  updatedAt?: string;
  verifiedAt?: string;
  taskTitle?: string;
  taskDescription?: string;
  rewardPoints?: number;
  taskCreatedBy?: string;
  taskType?: 'SINGLE' | 'MULTIPLE';
  evidenceRequired?: boolean;
  minNotesLength?: number;
  rubricChecklist?: string[];
}
