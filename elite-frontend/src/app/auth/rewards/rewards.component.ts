import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { RewardService } from '../../core/services/reward.service';
import { RewardTransaction } from '../../core/models/reward.model';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-rewards',
  templateUrl: './rewards.component.html',
  styleUrls: ['./rewards.component.scss'],
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ButtonModule, TableModule]
})
export class RewardsComponent implements OnInit {
  walletBalance: number = 0;
  transactions: RewardTransaction[] = [];
  loading: boolean = true;
  transactionLoading: boolean = true;
  currentUserId: string = '';
  
  // For UI display options
  showBalance: boolean = true;
  viewMode: 'all' | 'earned' | 'spent' = 'all';
  
  constructor(
    private rewardService: RewardService,
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
    this.rewardService.getWalletBalance(this.currentUserId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (balance) => {
          this.walletBalance = balance;
        },
        error: (error) => {
          console.error('Error loading wallet balance', error);
          this.toastService.showError('Failed to load wallet balance');
        }
      });
  }
  
  loadTransactions(): void {
    this.transactionLoading = true;
    this.rewardService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.transactionLoading = false))
      .subscribe({
        next: (transactions) => {
          this.transactions = transactions;
        },
        error: (error) => {
          console.error('Error loading transactions', error);
          this.toastService.showError('Failed to load transaction history');
        }
      });
  }
  
  toggleBalanceVisibility(): void {
    this.showBalance = !this.showBalance;
  }
  
  setViewMode(mode: 'all' | 'earned' | 'spent'): void {
    this.viewMode = mode;
  }
  
  getFilteredTransactions(): RewardTransaction[] {
    if (this.viewMode === 'all') {
      return this.transactions;
    }
    
    return this.transactions.filter(t => 
      t.transactionType === this.viewMode.toUpperCase() as 'EARNED' | 'SPENT'
    );
  }
  
  // Helper methods for summary calculations
  getTotalEarnedPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === 'EARNED')
      .reduce((sum, t) => sum + t.points, 0);
  }
  
  getTotalSpentPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === 'SPENT')
      .reduce((sum, t) => sum + t.points, 0);
  }
  
  // Helper methods for UI display
  getTransactionIcon(type: string): string {
    return type === 'EARNED' ? 'add_circle' : 'remove_circle';
  }
  
  getTransactionColor(type: string): string {
    return type === 'EARNED' ? 'text-success' : 'text-danger';
  }
  
  formatPoints(transaction: RewardTransaction): string {
    return (transaction.transactionType === 'EARNED' ? '+' : '-') + transaction.points;
  }
  
  formatDate(dateStr: string): string {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  }
} 