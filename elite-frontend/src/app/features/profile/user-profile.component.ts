import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { Select } from 'primeng/select';
import { InputNumberModule } from 'primeng/inputnumber';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { WalletService } from '../../core/services/wallet.service';
import { Transaction } from '../../core/models/wallet.model';
import { TaskService } from '../../core/services/task.service';
import { StoreService } from '../../core/services/store.service';
import { StorePurchase } from '../../core/models/store-item.model';

interface UserTaskDisplay {
  id: string;
  title: string;
  status: string;
  submittedAt?: string;
  rewardPoints: number;
  feedbackNotes?: string;
}

interface ContributionEvent {
  date: string;
  kind: 'TASK' | 'CLAIM' | 'CREDIT' | 'DEBIT';
  title: string;
  detail: string;
}

@Component({
    selector: 'app-user-profile',
    templateUrl: './user-profile.component.html',
    styleUrls: ['./user-profile.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        Tabs, TabList, Tab, TabPanels, TabPanel,
        TableModule,
        ButtonModule,
        InputTextModule,
        Select,
        InputNumberModule,
        ToastModule
    ],
    providers: [MessageService],
    changeDetection: ChangeDetectionStrategy.Eager
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  rewardPoints = 0;
  transactions: Transaction[] = [];
  userTasks: UserTaskDisplay[] = [];
  storeClaims: StorePurchase[] = [];
  contributionTimeline: ContributionEvent[] = [];

  editing = false;
  saving = false;
  editForm = {
    name: '',
    email: '',
    mobileNumber: '',
    age: null as number | null,
    gender: '',
    address: '',
    emergencyContact: ''
  };

  genderOptions = [
    { label: 'Male', value: 'MALE' },
    { label: 'Female', value: 'FEMALE' },
    { label: 'Other', value: 'OTHER' }
  ];

  constructor(
    private userService: UserService,
    private walletService: WalletService,
    private taskService: TaskService,
    private storeService: StoreService,
    private router: Router,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile(): void {
    this.user = this.userService.getCurrentUser();
    if (!this.user) {
      this.userService.getUserProfile().subscribe({
        next: (res) => {
          if (res.success && res.data) {
            this.user = res.data;
            this.syncEditForm();
            this.loadRoleData();
          }
        }
      });
      return;
    }

    this.syncEditForm();
    this.loadRoleData();
  }

  private syncEditForm(): void {
    if (!this.user) return;
    this.editForm = {
      name: this.user.name || '',
      email: this.user.email || '',
      mobileNumber: this.user.mobileNumber || '',
      age: this.user.age ?? null,
      gender: this.user.gender || '',
      address: this.user.address || '',
      emergencyContact: this.user.emergencyContact || ''
    };
  }

  private loadRoleData(): void {
    if (!this.user || this.user.role !== 'STUDENT' || !this.user.eliteId) {
      return;
    }
    this.loadRewardPoints(this.user.eliteId);
    this.loadTransactions(this.user.eliteId);
    this.loadUserTasks(this.user.eliteId);
    this.loadStoreClaims(this.user.eliteId);
  }

  loadRewardPoints(userId: string): void {
    this.walletService.getWalletBalance(userId).subscribe((points) => {
      this.rewardPoints = points;
    });
  }

  loadTransactions(userId: string): void {
    this.walletService.getTransactionHistory(userId).subscribe((transactions) => {
      this.transactions = transactions;
      this.rebuildContributionTimeline();
    });
  }

  loadUserTasks(userId: string): void {
    this.taskService.getSubmissionsByStudent(userId).subscribe((submissions) => {
      this.userTasks = submissions.map(s => ({
        id: s.id || '',
        title: s.taskTitle || 'Task',
        status: s.status || 'UNKNOWN',
        submittedAt: s.submittedAt,
        rewardPoints: s.rewardPoints || 0,
        feedbackNotes: s.feedbackNotes
      }));
      this.rebuildContributionTimeline();
    });
  }

  loadStoreClaims(userId: string): void {
    this.storeService.getPurchasesForStudent(userId).subscribe({
      next: (claims) => {
        this.storeClaims = claims || [];
        this.rebuildContributionTimeline();
      },
      error: () => {
        this.storeClaims = [];
      }
    });
  }

  private rebuildContributionTimeline(): void {
    const events: ContributionEvent[] = [];

    this.userTasks
      .filter(t => t.status === 'COMPLETED')
      .forEach(t => {
        events.push({
          date: t.submittedAt || '',
          kind: 'TASK',
          title: t.title,
          detail: `Verified contribution (+${t.rewardPoints} Elite Points)`
        });
      });

    this.storeClaims.forEach(c => {
      events.push({
        date: c.claimedAt,
        kind: 'CLAIM',
        title: c.itemName,
        detail: 'Obtained from school store'
      });
    });

    this.transactions.forEach(tx => {
      const desc = tx.description || '';
      const isAdminAdjust = /admin|adjust|grant|credit/i.test(desc) && !/task completion/i.test(desc);
      events.push({
        date: tx.createdAt || '',
        kind: tx.transactionType === 'CREDIT' ? 'CREDIT' : 'DEBIT',
        title: desc || (tx.transactionType === 'CREDIT' ? 'Points credited' : 'Points used'),
        detail: tx.transactionType === 'CREDIT'
          ? (isAdminAdjust
            ? `Admin adjustment (+${tx.points})`
            : `+${tx.points} Elite Points`)
          : `−${tx.points} Elite Points`
      });
    });

    this.contributionTimeline = events
      .filter(e => !!e.date)
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
  }

  startEdit(): void {
    this.syncEditForm();
    this.editing = true;
  }

  cancelEdit(): void {
    this.editing = false;
    this.syncEditForm();
  }

  saveProfile(): void {
    if (!this.user?.eliteId) return;

    this.saving = true;
    this.userService.updateUserProfile(this.user.eliteId, {
      name: this.editForm.name,
      email: this.editForm.email,
      mobileNumber: this.editForm.mobileNumber || undefined,
      age: this.editForm.age ?? undefined,
      gender: this.editForm.gender || undefined,
      address: this.editForm.address || undefined,
      emergencyContact: this.editForm.emergencyContact || undefined
    }).subscribe({
      next: (res) => {
        this.saving = false;
        if (res.success && res.data) {
          this.user = res.data;
          this.editing = false;
          this.messageService.add({ severity: 'success', summary: 'Saved', detail: 'Profile updated' });
        } else {
          this.messageService.add({ severity: 'error', summary: 'Error', detail: res.message || 'Failed to update profile' });
        }
      },
      error: (err) => {
        this.saving = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err.error?.message || 'Failed to update profile'
        });
      }
    });
  }

  formatDate(date: string): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString();
  }

  navigateToStore(): void {
    this.router.navigate(['/store']);
  }
}
