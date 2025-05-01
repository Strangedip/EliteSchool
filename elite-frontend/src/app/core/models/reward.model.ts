export interface RewardTransaction {
  id: string;
  studentId: string;
  points: number;
  transactionType: 'EARNED' | 'SPENT';
  description: string;
  createdAt: string;
} 