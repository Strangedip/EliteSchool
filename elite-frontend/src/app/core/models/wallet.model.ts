/**
 * Represents a wallet for a student
 */
export interface Wallet {
    studentId: string;
    balance: number;
}

/**
 * Transaction types
 */
export enum TransactionType {
    CREDIT = 'CREDIT',
    DEBIT = 'DEBIT'
}

/**
 * Represents a transaction in the wallet
 */
export interface Transaction {
    id: string;
    studentId: string;
    transactionType: TransactionType;
    points: number;
    description: string;
    createdAt: string;
} 