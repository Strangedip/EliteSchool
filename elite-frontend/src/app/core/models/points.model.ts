export interface PointsAccount {
  studentId: string;
  balance: number;
}

export enum TransactionType {
  CREDIT = 'CREDIT',
  DEBIT = 'DEBIT'
}

export interface Transaction {
  id: string;
  studentId: string;
  studentName?: string;
  transactionType: TransactionType;
  source?: string;
  points: number;
  description: string;
  createdAt: string;
}
