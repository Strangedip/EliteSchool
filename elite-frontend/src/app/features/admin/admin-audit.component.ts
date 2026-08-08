import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { StoreService } from '../../core/services/store.service';
import { SupportService } from '../../core/services/support.service';
import { WalletService } from '../../core/services/wallet.service';
import { UserService } from '../../core/services/user.service';
import { StorePurchase } from '../../core/models/store-item.model';
import { SupportTicket } from '../../core/models/support.model';
import { Transaction } from '../../core/models/wallet.model';
import { User } from '../../core/models/user.model';
import { ContributionNomination } from '../../core/models/nomination.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
    selector: 'app-admin-audit',
    imports: [CommonModule, Tabs, TabList, Tab, TabPanels, TabPanel, TableModule, TagModule, ToastModule],
    providers: [MessageService],
    templateUrl: './admin-audit.component.html',
    changeDetection: ChangeDetectionStrategy.Eager,
    styleUrls: ['./admin-audit.component.scss']
})
export class AdminAuditComponent implements OnInit {
  claims: StorePurchase[] = [];
  tickets: SupportTicket[] = [];
  nominations: ContributionNomination[] = [];
  adminCredits: { studentId: string; studentName: string; points: number; description: string; createdAt: string }[] = [];
  private studentNames = new Map<string, string>();
  loading = true;

  constructor(
    private storeService: StoreService,
    private supportService: SupportService,
    private walletService: WalletService,
    private userService: UserService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    forkJoin({
      claims: this.storeService.getAllPurchases().pipe(catchError(() => of([] as StorePurchase[]))),
      tickets: this.supportService.getAllTickets().pipe(catchError(() => of([] as SupportTicket[]))),
      nominations: this.walletService.listNominations().pipe(catchError(() => of([] as ContributionNomination[]))),
      students: this.userService.getAllStudents().pipe(catchError(() => of({ success: false, data: [] as User[] })))
    }).subscribe({
      next: ({ claims, tickets, nominations, students }) => {
        this.claims = claims || [];
        this.tickets = (tickets || []).filter(t =>
          t.status === 'RESOLVED' || t.status === 'CLOSED'
        );
        this.nominations = (nominations || []).filter(n =>
          n.status === 'APPROVED' || n.status === 'REJECTED'
        );
        const studentList = students?.data || [];
        this.studentNames = new Map(
          studentList
            .filter(s => s.eliteId)
            .map(s => [s.eliteId!, s.name || s.username || s.eliteId!])
        );
        this.loadAdminCredits(studentList);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to load audit data' });
      }
    });
  }

  studentLabel(studentId: string): string {
    return this.studentNames.get(studentId) || studentId;
  }

  private loadAdminCredits(students: User[]): void {
    if (!students.length) {
      this.adminCredits = [];
      return;
    }
    const limited = students.slice(0, 40);
    forkJoin(
      limited.map(s =>
        this.walletService.getTransactionHistory(s.eliteId!).pipe(
          catchError(() => of([] as Transaction[])),
        )
      )
    ).subscribe(histories => {
      const rows: typeof this.adminCredits = [];
      histories.forEach((txs, idx) => {
        const student = limited[idx];
        (txs || [])
          .filter(t => t.transactionType === 'CREDIT')
          .filter(t => {
            const d = (t.description || '').toLowerCase();
            return d.includes('admin') || d.includes('adjust') || d.includes('grant') || d.includes('manual');
          })
          .forEach(t => {
            rows.push({
              studentId: student.eliteId || '',
              studentName: student.name || student.username || '',
              points: t.points,
              description: t.description || '',
              createdAt: t.createdAt || ''
            });
          });
      });
      this.adminCredits = rows.sort((a, b) =>
        new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
    });
  }
}
