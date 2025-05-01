import { Component, OnInit } from '@angular/core';
import { RewardService } from '../../core/services/reward.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { finalize } from 'rxjs';
import { RewardTransaction } from '../../core/models/reward.model';

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
    private rewardService: RewardService,
    private authService: AuthService,
    private userService: UserService,
    private toastService: ToastService
  ) {}
  
  ngOnInit(): void {
    this.loadUserInfo();
  }
  
  loadUserInfo(): void {
    // Try to get current user from auth service
    const user = this.userService.getCurrentUser();
    if (user) {
      // If user found, get ID
      this.currentUserId = user.id || user.eliteId || '';
      if (this.currentUserId) {
        this.loadRewardData();
      }
    } else {
      // If no user found, try to fetch from API
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserId = response.data.id || response.data.eliteId || '';
            if (this.currentUserId) {
              this.loadRewardData();
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
  
  loadRewardData(): void {
    if (!this.currentUserId) return;
    
    this.loadingRewards = true;
    
    // Load reward points
    this.rewardService.getWalletBalance(this.currentUserId)
      .subscribe({
        next: (balance) => {
          this.rewardPoints = balance;
        },
        error: (error) => {
          console.error('Error loading wallet balance', error);
        }
      });
      
    // Load transaction history to calculate totals
    this.rewardService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.loadingRewards = false))
      .subscribe({
        next: (transactions) => {
          this.calculateRewardTotals(transactions);
        },
        error: (error) => {
          console.error('Error loading transaction history', error);
        }
      });
  }
  
  calculateRewardTotals(transactions: RewardTransaction[]): void {
    this.totalEarned = transactions
      .filter(t => t.transactionType === 'EARNED')
      .reduce((sum, t) => sum + t.points, 0);
      
    this.totalSpent = transactions
      .filter(t => t.transactionType === 'SPENT')
      .reduce((sum, t) => sum + t.points, 0);
  }
}
