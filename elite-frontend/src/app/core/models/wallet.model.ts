export interface Wallet {
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
  transactionType: TransactionType;
  points: number;
  description: string;
  createdAt: string;
}
