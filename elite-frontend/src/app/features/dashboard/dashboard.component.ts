import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WalletService } from '../../core/services/wallet.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { finalize } from 'rxjs';
import { Transaction } from '../../core/models/wallet.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [CommonModule]
})
export class DashboardComponent implements OnInit {
  rewardPoints = 0;
  totalEarned = 0;
  totalSpent = 0;
  loadingRewards = false;
  currentUserId = '';
  
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
    const user = this.userService.getCurrentUser();
    if (user) {
      this.currentUserId = user.eliteId || '';
      if (this.currentUserId) {
        this.loadWalletData();
      }
    } else {
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserId = response.data.eliteId || '';
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
    
    this.walletService.getWalletBalance(this.currentUserId).subscribe({
      next: (balance: number) => this.rewardPoints = balance,
      error: (error: any) => console.error('Error loading wallet balance', error)
    });
      
    this.walletService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.loadingRewards = false))
      .subscribe({
        next: (transactions: Transaction[]) => this.calculateWalletTotals(transactions),
        error: (error: any) => console.error('Error loading transaction history', error)
      });
  }
  
  calculateWalletTotals(transactions: Transaction[]): void {
    this.totalEarned = transactions.filter(t => t.transactionType === 'CREDIT').reduce((sum, t) => sum + t.points, 0);
    this.totalSpent = transactions.filter(t => t.transactionType === 'DEBIT').reduce((sum, t) => sum + t.points, 0);
  }
}
