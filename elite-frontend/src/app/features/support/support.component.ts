import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

import {
  SupportCategory,
  SupportTicket,
  SupportTicketStatus
} from '../../core/models/support.model';
import { SupportService } from '../../core/services/support.service';
import { UserService } from '../../core/services/user.service';

const CATEGORY_OPTIONS: { label: string; value: SupportCategory }[] = [
  { label: 'Concern', value: 'CONCERN' },
  { label: 'Issue', value: 'ISSUE' },
  { label: 'Misalignment', value: 'MISALIGNMENT' },
  { label: 'Other', value: 'OTHER' }
];

const STATUS_OPTIONS: { label: string; value: SupportTicketStatus }[] = [
  { label: 'Open', value: 'OPEN' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Resolved', value: 'RESOLVED' },
  { label: 'Closed', value: 'CLOSED' }
];

const STAFF_STATUS_OPTIONS: { label: string; value: SupportTicketStatus }[] = [
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Resolved', value: 'RESOLVED' },
  { label: 'Closed', value: 'CLOSED' }
];

@Component({
    selector: 'app-support',
    templateUrl: './support.component.html',
    styleUrls: ['./support.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        Textarea,
        Select,
        TableModule,
        TagModule,
        ToastModule
    ],
    providers: [MessageService],
    changeDetection: ChangeDetectionStrategy.Eager
})
export class SupportComponent implements OnInit {
  tickets: SupportTicket[] = [];
  loading = false;
  currentUserRole = '';
  private studentNames = new Map<string, string>();

  categoryOptions = CATEGORY_OPTIONS;
  statusOptions = STATUS_OPTIONS;
  staffStatusOptions = STAFF_STATUS_OPTIONS;
  statusFilter: SupportTicketStatus | null = null;

  createDialogVisible = false;
  detailDialogVisible = false;
  saving = false;

  newTicket: SupportTicket = this.emptyTicket();
  selectedTicket: SupportTicket | null = null;
  replyBody = '';
  statusUpdate: SupportTicketStatus = 'IN_PROGRESS';
  resolutionNotes = '';

  constructor(
    private supportService: SupportService,
    private userService: UserService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    const user = this.userService.getCurrentUser();
    this.currentUserRole = user?.role?.toUpperCase() || '';
    this.loadTickets();
    if (this.isStaff()) {
      this.loadStudentNames();
    }
  }

  isStaff(): boolean {
    return ['FACULTY', 'ADMIN', 'MANAGEMENT'].includes(this.currentUserRole);
  }

  isStudent(): boolean {
    return this.currentUserRole === 'STUDENT';
  }

  canStudentReply(): boolean {
    if (!this.selectedTicket?.status) return false;
    return this.isStudent() && ['OPEN', 'IN_PROGRESS'].includes(this.selectedTicket.status);
  }

  studentLabel(ticket: SupportTicket | null | undefined): string {
    if (!ticket) return '';
    if (ticket.studentName?.trim()) {
      return ticket.studentName;
    }
    if (ticket.studentId && this.studentNames.has(ticket.studentId)) {
      return this.studentNames.get(ticket.studentId)!;
    }
    return ticket.studentId || 'Unknown student';
  }

  loadTickets(): void {
    this.loading = true;
    const request$ = this.isStaff()
      ? this.supportService.getAllTickets(this.statusFilter)
      : this.supportService.getMyTickets();

    request$.subscribe({
      next: (tickets) => {
        this.tickets = tickets;
        this.loading = false;
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to load tickets: ${error.error?.message || error.message}`
        });
        this.loading = false;
      }
    });
  }

  private loadStudentNames(): void {
    this.userService.getAllStudents().subscribe({
      next: (res) => {
        const users = res.data || [];
        this.studentNames = new Map(
          users
            .filter(u => !!u.eliteId)
            .map(u => [u.eliteId!, `${u.name || u.username}`])
        );
      },
      error: () => {}
    });
  }

  openCreateDialog(): void {
    this.newTicket = this.emptyTicket();
    this.createDialogVisible = true;
  }

  createTicket(): void {
    if (!this.newTicket.subject?.trim() || !this.newTicket.body?.trim() || !this.newTicket.category) {
      return;
    }
    this.saving = true;
    this.supportService.createTicket(this.newTicket).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Submitted', detail: 'Your support ticket was created.' });
        this.createDialogVisible = false;
        this.saving = false;
        this.loadTickets();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || 'Failed to create ticket'
        });
        this.saving = false;
      }
    });
  }

  openTicket(ticket: SupportTicket): void {
    if (!ticket.id) return;
    this.supportService.getTicket(ticket.id).subscribe({
      next: (full) => {
        this.selectedTicket = full;
        this.replyBody = '';
        this.resolutionNotes = full.resolutionNotes || '';
        this.statusUpdate = (full.status && full.status !== 'OPEN') ? full.status : 'IN_PROGRESS';
        this.detailDialogVisible = true;
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || 'Failed to load ticket'
        });
      }
    });
  }

  sendReply(): void {
    if (!this.selectedTicket?.id || !this.replyBody.trim()) return;
    this.saving = true;
    this.supportService.addMessage(this.selectedTicket.id, this.replyBody.trim()).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Sent', detail: 'Reply added.' });
        this.replyBody = '';
        this.saving = false;
        this.refreshSelected();
        this.loadTickets();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || 'Failed to send reply'
        });
        this.saving = false;
      }
    });
  }

  updateStatus(): void {
    if (!this.selectedTicket?.id) return;
    this.saving = true;
    this.supportService.updateStatus(this.selectedTicket.id, {
      status: this.statusUpdate,
      resolutionNotes: this.resolutionNotes?.trim() || undefined
    }).subscribe({
      next: (updated) => {
        this.selectedTicket = updated;
        this.messageService.add({ severity: 'success', summary: 'Updated', detail: 'Ticket status updated.' });
        this.saving = false;
        this.loadTickets();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || 'Failed to update status'
        });
        this.saving = false;
      }
    });
  }

  statusSeverity(status?: SupportTicketStatus): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    switch (status) {
      case 'OPEN': return 'warn';
      case 'IN_PROGRESS': return 'info';
      case 'RESOLVED': return 'success';
      case 'CLOSED': return 'secondary';
      default: return 'contrast';
    }
  }

  categoryLabel(category?: SupportCategory): string {
    return this.categoryOptions.find(c => c.value === category)?.label || category || '';
  }

  statusLabel(status?: SupportTicketStatus): string {
    return this.statusOptions.find(s => s.value === status)?.label || status || '';
  }

  private refreshSelected(): void {
    if (!this.selectedTicket?.id) return;
    this.supportService.getTicket(this.selectedTicket.id).subscribe({
      next: (full) => {
        this.selectedTicket = full;
        this.resolutionNotes = full.resolutionNotes || '';
      }
    });
  }

  private emptyTicket(): SupportTicket {
    return { subject: '', body: '', category: 'CONCERN' };
  }
}
