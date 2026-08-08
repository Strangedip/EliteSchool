import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { Select } from 'primeng/select';
import { InputNumber } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { WalletService } from '../../core/services/wallet.service';
import { Transaction } from '../../core/models/wallet.model';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { User } from '../../core/models/user.model';
import { finalize } from 'rxjs';

@Component({
    selector: 'app-wallet',
    templateUrl: './wallet.component.html',
    styleUrls: ['./wallet.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [
        CommonModule,
        RouterModule,
        FormsModule,
        ButtonModule,
        TableModule,
        Select,
        InputNumber,
        InputTextModule
    ]
})
export class WalletComponent implements OnInit {
  walletBalance = 0;
  transactions: Transaction[] = [];
  loading = true;
  transactionLoading = true;
  currentUserId = '';
  currentUserRole = '';
  isAdmin = false;

  showBalance = false;
  viewMode: 'all' | 'credit' | 'debit' = 'all';

  students: { label: string; value: string }[] = [];
  selectedStudentId = '';
  adjustPoints = 10;
  adjustDescription = '';
  adjusting = false;

  constructor(
    private walletService: WalletService,
    private userService: UserService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.userService.getUserProfile().subscribe({
      next: (response) => {
        const user = response.data || this.userService.getCurrentUser();
        this.currentUserId = user?.eliteId || '';
        this.currentUserRole = (user?.role || '').toUpperCase();
        this.isAdmin = ['ADMIN', 'MANAGEMENT'].includes(this.currentUserRole);

        if (this.isAdmin) {
          this.loadStudents();
          this.loading = false;
          this.transactionLoading = false;
        } else if (this.currentUserId) {
          this.loadWalletBalance();
          this.loadTransactions();
        }
      },
      error: () => this.toastService.showError('Failed to load user profile')
    });
  }

  private loadStudents(): void {
    this.userService.getAllStudents().subscribe({
      next: (response) => {
        const list = response.data ?? [];
        this.students = list.map((u: User) => ({
          label: `${u.name} (${u.username})`,
          value: u.eliteId
        }));
      },
      error: () => this.toastService.showError('Failed to load students')
    });
  }

  onStudentSelected(): void {
    if (!this.selectedStudentId) return;
    this.currentUserId = this.selectedStudentId;
    this.loadWalletBalance();
    this.loadTransactions();
  }

  loadWalletBalance(): void {
    if (!this.currentUserId) return;
    this.loading = true;
    this.walletService.getWalletBalance(this.currentUserId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (balance: number) => this.walletBalance = balance,
        error: () => this.toastService.showError('Failed to load wallet balance')
      });
  }

  loadTransactions(): void {
    if (!this.currentUserId) return;
    this.transactionLoading = true;
    this.walletService.getTransactionHistory(this.currentUserId)
      .pipe(finalize(() => this.transactionLoading = false))
      .subscribe({
        next: (transactions: Transaction[]) => this.transactions = transactions,
        error: () => this.toastService.showError('Failed to load transaction history')
      });
  }

  applyCredit(): void {
    this.adjustBalance(true);
  }

  applyDebit(): void {
    this.adjustBalance(false);
  }

  private adjustBalance(credit: boolean): void {
    if (!this.selectedStudentId || !this.adjustPoints || this.adjustPoints < 1) {
      this.toastService.showError('Select a student and enter positive points');
      return;
    }
    const description = this.adjustDescription?.trim()
      || (credit ? 'Admin credit adjustment' : 'Admin debit adjustment');
    this.adjusting = true;
    const request$ = credit
      ? this.walletService.creditPoints(this.selectedStudentId, this.adjustPoints, description)
      : this.walletService.debitPoints(this.selectedStudentId, this.adjustPoints, description);

    request$.pipe(finalize(() => this.adjusting = false)).subscribe({
      next: (balance) => {
        this.walletBalance = balance;
        this.toastService.showSuccess(credit ? 'Points credited' : 'Points debited');
        this.loadTransactions();
      },
      error: () => {}
    });
  }

  toggleBalanceVisibility(): void {
    this.showBalance = !this.showBalance;
  }

  setViewMode(mode: 'all' | 'credit' | 'debit'): void {
    this.viewMode = mode;
  }

  getFilteredTransactions(): Transaction[] {
    if (this.viewMode === 'all') return this.transactions;
    return this.transactions.filter(t =>
      this.viewMode === 'credit' ? t.transactionType === 'CREDIT' : t.transactionType === 'DEBIT'
    );
  }

  getTotalCreditPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === 'CREDIT')
      .reduce((sum, t) => sum + t.points, 0);
  }

  getTotalDebitPoints(): number {
    return this.transactions
      .filter(t => t.transactionType === 'DEBIT')
      .reduce((sum, t) => sum + t.points, 0);
  }

  formatDate(date: string): string {
    return date ? new Date(date).toLocaleString() : '';
  }

  formatPoints(transaction: Transaction): string {
    const sign = transaction.transactionType === 'CREDIT' ? '+' : '-';
    return `${sign}${transaction.points}`;
  }

  getTransactionColor(type: string): string {
    return type === 'CREDIT' ? 'credit-text' : 'debit-text';
  }
}
