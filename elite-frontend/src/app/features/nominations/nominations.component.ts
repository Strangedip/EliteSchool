import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { InputNumber } from 'primeng/inputnumber';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import { ContributionNomination, NominationStatus } from '../../core/models/nomination.model';
import { User } from '../../core/models/user.model';
import { WalletService } from '../../core/services/wallet.service';
import { UserService } from '../../core/services/user.service';

@Component({
    selector: 'app-nominations',
    templateUrl: './nominations.component.html',
    styleUrls: ['./nominations.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        Textarea,
        InputNumber,
        Select,
        TableModule,
        TagModule,
        ToastModule
    ],
    changeDetection: ChangeDetectionStrategy.Eager,
    providers: [MessageService]
})
export class NominationsComponent implements OnInit {
  nominations: ContributionNomination[] = [];
  students: { label: string; value: string }[] = [];
  loading = false;
  currentUserRole = '';
  createVisible = false;
  saving = false;

  statusFilter: NominationStatus | null = 'PENDING';
  statusOptions = [
    { label: 'Pending', value: 'PENDING' },
    { label: 'Approved', value: 'APPROVED' },
    { label: 'Rejected', value: 'REJECTED' },
    { label: 'All', value: null }
  ];

  draft: ContributionNomination = this.emptyDraft();

  reviewVisible = false;
  reviewMode: 'APPROVE' | 'REJECT' = 'APPROVE';
  reviewTarget: ContributionNomination | null = null;
  reviewNotes = '';
  reviewPoints: number | null = null;

  constructor(
    private walletService: WalletService,
    private userService: UserService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.userService.getUserProfile().subscribe({
      next: (res) => {
        this.currentUserRole = (res.data?.role || '').toUpperCase();
        this.load();
        if (this.canNominate() || this.canApprove()) {
          this.loadStudents();
        }
      },
      error: () => this.load()
    });
  }

  canNominate(): boolean {
    return ['FACULTY', 'MANAGEMENT', 'ADMIN'].includes(this.currentUserRole);
  }

  canApprove(): boolean {
    return this.currentUserRole === 'ADMIN';
  }

  emptyDraft(): ContributionNomination {
    return { studentId: '', suggestedPoints: 10, reason: '', evidenceNote: '' };
  }

  load(): void {
    this.loading = true;
    this.walletService.listNominations(this.statusFilter).subscribe({
      next: (list) => {
        this.nominations = list || [];
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load nominations' });
      }
    });
  }

  loadStudents(): void {
    this.userService.getAllStudents().subscribe({
      next: (res) => {
        const users: User[] = res.data || [];
        this.students = users
          .filter(u => u.eliteId)
          .map(u => ({
            label: `${u.name || u.username} (${u.eliteId})`,
            value: u.eliteId!
          }));
      },
      error: () => {}
    });
  }

  openCreate(): void {
    this.draft = this.emptyDraft();
    this.createVisible = true;
  }

  saveNomination(): void {
    if (!this.draft.studentId || !this.draft.reason?.trim() || !this.draft.suggestedPoints) {
      this.messageService.add({ severity: 'warn', summary: 'Missing fields', detail: 'Student, reason, and points are required' });
      return;
    }
    this.saving = true;
    this.walletService.createNomination(this.draft).subscribe({
      next: () => {
        this.saving = false;
        this.createVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Submitted', detail: 'Nomination sent for admin review' });
        this.load();
      },
      error: (err) => {
        this.saving = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err.error?.message || err.error?.friendlyMessage || 'Failed to create nomination'
        });
      }
    });
  }

  openReview(n: ContributionNomination, mode: 'APPROVE' | 'REJECT'): void {
    this.reviewTarget = n;
    this.reviewMode = mode;
    this.reviewNotes = '';
    this.reviewPoints = n.suggestedPoints ?? null;
    this.reviewVisible = true;
  }

  confirmReview(): void {
    if (!this.reviewTarget?.id) return;
    this.saving = true;
    const id = this.reviewTarget.id;
    const notes = this.reviewNotes?.trim() || undefined;

    if (this.reviewMode === 'APPROVE') {
      const points = this.reviewPoints && this.reviewPoints > 0 ? this.reviewPoints : undefined;
      this.walletService.approveNomination(id, points, notes).subscribe({
        next: () => {
          this.saving = false;
          this.reviewVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Approved', detail: 'Elite Points credited from nomination' });
          this.load();
        },
        error: (err) => {
          this.saving = false;
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: err.error?.message || err.error?.friendlyMessage || 'Approve failed'
          });
        }
      });
      return;
    }

    this.walletService.rejectNomination(id, notes).subscribe({
      next: () => {
        this.saving = false;
        this.reviewVisible = false;
        this.messageService.add({ severity: 'info', summary: 'Rejected', detail: 'Nomination rejected' });
        this.load();
      },
      error: (err) => {
        this.saving = false;
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: err.error?.message || err.error?.friendlyMessage || 'Reject failed'
        });
      }
    });
  }

  statusSeverity(status?: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'APPROVED': return 'success';
      case 'PENDING': return 'warn';
      case 'REJECTED': return 'danger';
      default: return 'secondary';
    }
  }

  studentLabel(studentId: string): string {
    return this.students.find(s => s.value === studentId)?.label || studentId;
  }
}
