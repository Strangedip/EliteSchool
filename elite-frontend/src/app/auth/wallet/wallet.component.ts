import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { WalletService } from '../../core/services/wallet.service';
import { Transaction, TransactionType } from '../../core/models/wallet.model';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-wallet',
  templateUrl: './wallet.component.html',
  styleUrls: ['./wallet.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ButtonModule, TableModule]
})
export class WalletComponent implements OnInit {
  walletBalance: number = 0;
  transactions: Transaction[] = [];
  loading: boolean = true;
  transactionLoading: boolean = true;
  currentUserId: string = '';
  
  // For UI display options
  showBalance: boolean = false;
  viewMode: 'all' | 'credit' | 'debit' = 'all';
  
  constructor(
    private walletService: WalletService,
    private authService: AuthService,
    private userService: UserService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }
  
  loadUserInfo(): void {
    // Try to get current user from user service
    const user = this.userService.getCurrentUser();
    if (user) {
      // If user found, get ID
      this.currentUserId = user.id || user.eliteId || '';
      if (this.currentUserId) {
        this.loadWalletBalance();
        this.loadTransactions();
      }
    } else {
      // If no user found, try to fetch from API
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserId = response.data.id || response.data.eliteId || '';
            if (this.currentUserId) {
              this.loadWalletBalance();
              this.loadTransactions();
            }
          }
        },
        error: (error) => {
          console.error('Failed to load user profile:', error);
          this.toastService.showError('Failed to load user profile');
        }
      });
    }
  }
  
  loadWalletBalance(): void {
    this.loading = true;
    this.walletService.getWalletBalance(this.currentUserId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (balance: number) => {
          this.walletBalance = balance;
        },
        error: (error: any) => {
          console.error('Error loading wallet balance', error);
          this.toastService.showError('Failed to load wallet balance');
        }
      });
  }
  
  loadTransactions(): void {
    this.transactionLoading = true;
    this.walletService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.transactionLoading = false))
      .subscribe({
        next: (transactions: Transaction[]) => {
          this.transactions = transactions;
        },
        error: (error: any) => {
          console.error('Error loading transactions', error);
          this.toastService.showError('Failed to load transaction history');
        }
      });
  }
  
  toggleBalanceVisibility(): void {
    this.showBalance = !this.showBalance;
  }
  
  setViewMode(mode: 'all' | 'credit' | 'debit'): void {
    this.viewMode = mode;
  }
  
  getFilteredTransactions(): Transaction[] {
    if (this.viewMode === 'all') {
      return this.transactions;
    }
    return this.transactions.filter(t => 
      t.transactionType === TransactionType[this.viewMode.toUpperCase() as keyof typeof TransactionType]
    );
  }
  
  // Helper methods for summary calculations
  getTotalCreditPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === TransactionType.CREDIT)
      .reduce((sum, t) => sum + t.points, 0);
  }
  
  getTotalDebitPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === TransactionType.DEBIT)
      .reduce((sum, t) => sum + t.points, 0);
  }
  
  // Helper methods for UI display
  getTransactionIcon(type: string): string {
    return type === 'CREDIT' ? 'add_circle' : 'remove_circle';
  }
  
  getTransactionColor(type: string): string {
    return type === 'CREDIT' ? 'text-success' : 'text-danger';
  }
  
  formatPoints(transaction: Transaction): string {
    return (transaction.transactionType === TransactionType.CREDIT ? '+' : '-') + transaction.points;
  }
  
  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  }
} 