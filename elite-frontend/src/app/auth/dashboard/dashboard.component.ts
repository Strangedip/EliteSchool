import { Component, OnInit } from '@angular/core';
import { WalletService } from '../../core/services/wallet.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { finalize } from 'rxjs';
import { Transaction } from '../../core/models/wallet.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  rewardPoints: number = 0;
  totalEarned: number = 0;
  totalSpent: number = 0;
  loadingRewards: boolean = false;
  currentUserId: string = '';
  
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
        this.loadWalletData();
      }
    } else {
      // If no user found, try to fetch from API
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserId = response.data.id || response.data.eliteId || '';
            if (this.currentUserId) {
              this.loadWalletData();
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
  
  loadWalletData(): void {
    if (!this.currentUserId) return;
    
    this.loadingRewards = true;
    
    // Load wallet balance
    this.walletService.getWalletBalance(this.currentUserId)
      .subscribe({
        next: (balance: number) => {
          this.rewardPoints = balance;
        },
        error: (error: any) => {
          console.error('Error loading wallet balance', error);
        }
      });
      
    // Load transaction history to calculate totals
    this.walletService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.loadingRewards = false))
      .subscribe({
        next: (transactions: Transaction[]) => {
          this.calculateWalletTotals(transactions);
        },
        error: (error: any) => {
          console.error('Error loading transaction history', error);
        }
      });
  }
  
  calculateWalletTotals(transactions: Transaction[]): void {
    this.totalEarned = transactions
      .filter(t => t.transactionType === 'CREDIT')
      .reduce((sum, t) => sum + t.points, 0);
      
    this.totalSpent = transactions
      .filter(t => t.transactionType === 'DEBIT')
      .reduce((sum, t) => sum + t.points, 0);
  }
}
