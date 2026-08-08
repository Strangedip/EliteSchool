import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';
import { Select } from 'primeng/select';
import { InputNumber } from 'primeng/inputnumber';
import { MultiSelect } from 'primeng/multiselect';
import { RouterModule } from '@angular/router';

import { AcquisitionType, ClaimWindowStatus, ItemCategory, StoreItem, StorePurchase } from '../../core/models/store-item.model';
import { StoreService } from '../../core/services/store.service';
import { UserService } from '../../core/services/user.service';
import { WalletService } from '../../core/services/wallet.service';
import { TaskService } from '../../core/services/task.service';
import { Task } from '../../core/models/task.model';
import { SelectButton } from 'primeng/selectbutton';
import { parseLines } from '../../core/utils/text.util';
import { ConfirmDialog } from 'primeng/confirmdialog';

@Component({
    selector: 'app-store',
    templateUrl: './store.component.html',
    styleUrls: ['./store.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        Textarea,
        ConfirmDialog,
        ToastModule,
        CardModule,
        TagModule,
        InputNumber,
        PanelModule,
        TableModule,
        Select,
        MultiSelect,
        SelectButton
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ConfirmationService, MessageService]
})
export class StoreComponent implements OnInit {
  items: StoreItem[] = [];
  filteredItems: StoreItem[] = [];
  searchQuery = '';
  categoryFilter: 'ALL' | ItemCategory = 'ALL';
  categoryFilterOptions = [
    { label: 'All', value: 'ALL' },
    { label: 'Materials', value: 'MATERIAL' },
    { label: 'Opportunities', value: 'OPPORTUNITY' }
  ];
  currentUserRole = '';
  readonly rewardPoints = signal(0);

  itemDialogVisible = false;
  editMode = false;
  showAdvancedStoreOptions = false;
  selectedItem: StoreItem | null = null;
  claimOpensLocal = '';
  claimClosesLocal = '';
  eligibilityChecklistText = '';

  imagePreviewVisible = false;
  previewImageUrl = '';

  historyDialogVisible = false;
  claimHistory: StorePurchase[] = [];
  loadingHistory = false;

  availableTasks: { label: string; value: string }[] = [];
  acquisitionOptions = [
    { label: 'Elite Points only', value: 'POINTS' as AcquisitionType },
    { label: 'Complete linked tasks only', value: 'TASKS' as AcquisitionType },
    { label: 'Tasks and Elite Points', value: 'POINTS_AND_TASKS' as AcquisitionType }
  ];
  categoryOptions = [
    { label: 'Material', value: 'MATERIAL' as ItemCategory },
    { label: 'Opportunity', value: 'OPPORTUNITY' as ItemCategory }
  ];

  constructor(private storeService: StoreService,
    private userService: UserService,
    private walletService: WalletService,
    private taskService: TaskService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadItems();
    if (this.isAdmin()) {
      this.loadAvailableTasks();
    }
  }

  loadUserInfo(): void {
    const user = this.userService.getCurrentUser();
    this.currentUserRole = (user?.role || '').toUpperCase();

    if (user && this.currentUserRole === 'STUDENT') {
      const userId = user.eliteId;
      if (userId) {
        this.loadWalletBalance(userId);
      }
    }
  }

  loadWalletBalance(userId: string): void {
    this.walletService.getWalletBalance(userId).subscribe({
      next: (balance) => {
        this.rewardPoints.set(balance);
        this.cdr.markForCheck();
      },
      error: (error) => {
        console.error('Error loading wallet balance:', error);
      }
    });
  }

  loadAvailableTasks(): void {
    this.taskService.getTasks().subscribe({
      next: (tasks: Task[]) => {
        this.availableTasks = (tasks || []).map(t => ({
          label: t.title,
          value: t.id
        }));
        this.cdr.markForCheck();
      },
      error: () => {
        this.availableTasks = [];
        this.cdr.markForCheck();
      }
    });
  }

  showPurchaseHistory(): void {
    this.loadingHistory = true;
    this.historyDialogVisible = true;
    this.cdr.markForCheck();

    const user = this.userService.getCurrentUser();
    const userId = user?.eliteId || '';

    if (userId) {
      this.storeService.getPurchasesForStudent(userId).subscribe({
        next: (claims) => {
          this.claimHistory = claims || [];
          this.loadingHistory = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load claim history'
          });
          this.loadingHistory = false;
          this.cdr.markForCheck();
        }
      });
    } else {
      this.loadingHistory = false;
      this.cdr.markForCheck();
    }
  }

  loadItems(): void {
    this.storeService.getAllItems().subscribe({
      next: (items) => {
        this.items = items.map(item => ({
          ...item,
          acquisitionType: item.acquisitionType || 'POINTS',
          itemCategory: item.itemCategory || 'MATERIAL',
          requiredTaskIds: item.requiredTaskIds || [],
          claims: item.claims || []
        }));
        this.filterItems();
        this.cdr.markForCheck();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to load store items: ${error.message}`
        });
      }
    });
  }

  filterItems(): void {
    let list = this.items;
    if (this.categoryFilter !== 'ALL') {
      list = list.filter(i => (i.itemCategory || 'MATERIAL') === this.categoryFilter);
    }
    if (this.searchQuery) {
      const q = this.searchQuery.toLowerCase();
      list = list.filter(item =>
        item.name.toLowerCase().includes(q) ||
        (item.description && item.description.toLowerCase().includes(q))
      );
    }
    this.filteredItems = list;
  }

  onCategoryFilterChange(): void {
    this.filterItems();
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterItems();
  }

  isAdmin(): boolean {
    const role = this.currentUserRole?.toUpperCase() || '';
    return ['ADMIN', 'MANAGEMENT'].includes(role);
  }

  windowStatusOf(item: StoreItem): ClaimWindowStatus {
    return item.windowStatus || 'OPEN';
  }

  isWindowOpen(item: StoreItem): boolean {
    if (typeof item.withinClaimWindow === 'boolean') {
      return item.withinClaimWindow;
    }
    return this.windowStatusOf(item) === 'OPEN';
  }

  tasksDoneCount(item: StoreItem): number {
    return (item.requiredTasks || []).filter(t => t.completed).length;
  }

  tasksTotalCount(item: StoreItem): number {
    return (item.requiredTasks || item.requiredTaskIds || []).length;
  }

  claimedByLabel(item: StoreItem): string {
    const claims = item.claims || [];
    if (!claims.length) {
      return '';
    }
    return claims
      .map(c => c.studentName || c.studentId?.slice(0, 8) || 'Student')
      .join(', ');
  }

  statusBadge(item: StoreItem): { label: string; severity: 'success' | 'info' | 'warn' | 'danger' | 'secondary' } {
    if (this.windowStatusOf(item) === 'EXPIRED') {
      return { label: 'Expired', severity: 'danger' };
    }
    if (this.windowStatusOf(item) === 'NOT_OPEN') {
      return { label: 'Not open yet', severity: 'warn' };
    }
    if (item.stock <= 0) {
      const who = this.claimedByLabel(item);
      return { label: who ? `Claimed — ${who}` : 'Out of stock — Claimed', severity: 'secondary' };
    }
    return { label: `${item.stock} in stock`, severity: 'success' };
  }

  acquisitionOf(item: StoreItem): AcquisitionType {
    return item.acquisitionType || 'POINTS';
  }

  acquisitionLabel(item: StoreItem): string {
    switch (this.acquisitionOf(item)) {
      case 'TASKS':
        return 'Task unlock';
      case 'POINTS_AND_TASKS':
        return 'Tasks + Points';
      default:
        return 'Points';
    }
  }

  acquisitionSeverity(item: StoreItem): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    switch (this.acquisitionOf(item)) {
      case 'TASKS':
        return 'info';
      case 'POINTS_AND_TASKS':
        return 'warn';
      default:
        return 'success';
    }
  }

  usesPoints(item: StoreItem): boolean {
    const type = this.acquisitionOf(item);
    return type === 'POINTS' || type === 'POINTS_AND_TASKS';
  }

  usesTasks(item: StoreItem): boolean {
    const type = this.acquisitionOf(item);
    return type === 'TASKS' || type === 'POINTS_AND_TASKS';
  }

  tasksEligible(item: StoreItem): boolean {
    if (!this.usesTasks(item)) {
      return true;
    }
    const tasks = item.requiredTasks || [];
    if (tasks.length) {
      return tasks.every(t => t.completed);
    }
    if (typeof item.eligible === 'boolean' && this.isWindowOpen(item) && item.stock > 0) {
      return item.eligible;
    }
    return false;
  }

  canGetItem(item: StoreItem): boolean {
    if (item.stock <= 0 || !this.isWindowOpen(item)) {
      return false;
    }
    const type = this.acquisitionOf(item);
    if (type === 'POINTS') {
      return this.rewardPoints() >= item.price;
    }
    if (type === 'TASKS') {
      return this.tasksEligible(item);
    }
    return this.tasksEligible(item) && this.rewardPoints() >= item.price;
  }

  ctaLabel(item: StoreItem): string {
    if (this.canGetItem(item)) {
      const type = this.acquisitionOf(item);
      if (type === 'TASKS') {
        return 'Claim';
      }
      if (type === 'POINTS_AND_TASKS') {
        return 'Get';
      }
      return 'Get with points';
    }
    return this.claimBlockReason(item);
  }

  /** Human-readable reason the claim CTA is disabled (also used as tooltip). */
  claimBlockReason(item: StoreItem): string {
    if (this.windowStatusOf(item) === 'EXPIRED') {
      return 'Claim window expired';
    }
    if (this.windowStatusOf(item) === 'NOT_OPEN') {
      return 'Not open yet';
    }
    if (item.stock <= 0) {
      return 'Claimed / out of stock';
    }
    const type = this.acquisitionOf(item);
    const tasksOk = this.tasksEligible(item);
    const pointsOk = this.rewardPoints() >= item.price;
    if (type === 'TASKS') {
      return tasksOk ? 'Ready to claim' : `Complete required tasks (${this.tasksDoneCount(item)} of ${this.tasksTotalCount(item)})`;
    }
    if (type === 'POINTS_AND_TASKS') {
      if (!tasksOk) {
        return `Complete required tasks (${this.tasksDoneCount(item)} of ${this.tasksTotalCount(item)})`;
      }
      if (!pointsOk) {
        return `Need ${item.price - this.rewardPoints()} more Elite Points`;
      }
      return 'Ready to claim';
    }
    if (!pointsOk) {
      return `Need ${item.price - this.rewardPoints()} more Elite Points`;
    }
    return 'Ready to claim';
  }

  claimAcquisitionLabel(type?: string): string {
    switch ((type || '').toUpperCase()) {
      case 'TASKS':
        return 'Tasks';
      case 'POINTS_AND_TASKS':
        return 'Tasks + points';
      default:
        return 'Elite Points';
    }
  }

  taskLink(taskId: string): string[] {
    return ['/tasks'];
  }

  taskQuery(taskId: string): { taskId: string } {
    return { taskId };
  }

  onAcquisitionChange(): void {
    if (!this.selectedItem) {
      return;
    }
    const type = this.acquisitionOf(this.selectedItem);
    if (type === 'POINTS') {
      this.selectedItem.requiredTaskIds = [];
      if (!this.selectedItem.price || this.selectedItem.price < 1) {
        this.selectedItem.price = 1;
      }
    } else if (type === 'TASKS') {
      this.selectedItem.price = 0;
    } else if (type === 'POINTS_AND_TASKS') {
      if (!this.selectedItem.price || this.selectedItem.price < 1) {
        this.selectedItem.price = 1;
      }
    }
  }

  formValid(): boolean {
    if (!this.selectedItem?.name?.trim()) {
      return false;
    }
    if (this.selectedItem.stock == null || this.selectedItem.stock < 0) {
      return false;
    }
    const type = this.acquisitionOf(this.selectedItem);
    if (type === 'POINTS' || type === 'POINTS_AND_TASKS') {
      if (!this.selectedItem.price || this.selectedItem.price < 1) {
        return false;
      }
    }
    if (type === 'TASKS' || type === 'POINTS_AND_TASKS') {
      if (!this.selectedItem.requiredTaskIds?.length) {
        return false;
      }
    }
    if (this.selectedItem.itemCategory === 'OPPORTUNITY' && !this.selectedItem.opportunityBrief?.trim()) {
      return false;
    }
    return true;
  }

  private toLocalInput(iso?: string | null): string {
    if (!iso) {
      return '';
    }
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) {
      return '';
    }
    const pad = (n: number) => String(n).padStart(2, '0');
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  private fromLocalInput(local: string): string | null {
    if (!local) {
      return null;
    }
    const d = new Date(local);
    return Number.isNaN(d.getTime()) ? null : d.toISOString();
  }

  addItem(): void {
    this.selectedItem = {
      id: '',
      name: '',
      description: '',
      price: 1,
      stock: 0,
      acquisitionType: 'POINTS',
      itemCategory: 'MATERIAL',
      opportunityBrief: '',
      intendedAudience: '',
      eligibilityChecklist: [],
      requiredTaskIds: []
    };
    this.claimOpensLocal = '';
    this.claimClosesLocal = '';
    this.eligibilityChecklistText = '';
    this.editMode = false;
    this.showAdvancedStoreOptions = false;
    this.itemDialogVisible = true;
    if (!this.availableTasks.length) {
      this.loadAvailableTasks();
    }
  }

  editItem(item: StoreItem): void {
    this.selectedItem = {
      ...item,
      acquisitionType: item.acquisitionType || 'POINTS',
      itemCategory: item.itemCategory || 'MATERIAL',
      opportunityBrief: item.opportunityBrief || '',
      intendedAudience: item.intendedAudience || '',
      eligibilityChecklist: [...(item.eligibilityChecklist || [])],
      requiredTaskIds: [...(item.requiredTaskIds || [])]
    };
    this.claimOpensLocal = this.toLocalInput(item.claimOpensAt);
    this.claimClosesLocal = this.toLocalInput(item.claimClosesAt);
    this.eligibilityChecklistText = (item.eligibilityChecklist || []).join('\n');
    this.editMode = true;
    this.showAdvancedStoreOptions = !!(
      item.claimOpensAt || item.claimClosesAt || item.imageUrl ||
      item.itemCategory === 'OPPORTUNITY'
    );
    this.itemDialogVisible = true;
    if (!this.availableTasks.length) {
      this.loadAvailableTasks();
    }
  }

  previewImage(imageUrl: string): void {
    this.previewImageUrl = imageUrl;
    this.imagePreviewVisible = true;
  }

  saveItem(): void {
    if (!this.selectedItem || !this.formValid()) {
      return;
    }

    this.onAcquisitionChange();

    const payload: StoreItem = {
      ...this.selectedItem,
      acquisitionType: this.acquisitionOf(this.selectedItem),
      itemCategory: this.selectedItem.itemCategory || 'MATERIAL',
      claimOpensAt: this.fromLocalInput(this.claimOpensLocal),
      claimClosesAt: this.fromLocalInput(this.claimClosesLocal),
      eligibilityChecklist: this.selectedItem.itemCategory === 'OPPORTUNITY'
        ? parseLines(this.eligibilityChecklistText)
        : [],
      opportunityBrief: this.selectedItem.itemCategory === 'OPPORTUNITY'
        ? (this.selectedItem.opportunityBrief || '')
        : undefined,
      intendedAudience: this.selectedItem.itemCategory === 'OPPORTUNITY'
        ? (this.selectedItem.intendedAudience || '')
        : undefined,
      requiredTaskIds: this.usesTasks(this.selectedItem)
        ? (this.selectedItem.requiredTaskIds || [])
        : []
    };

    const operation = this.editMode
      ? this.storeService.updateItem(payload.id, payload)
      : this.storeService.addItem(payload);

    operation.subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: `Item ${this.editMode ? 'updated' : 'added'} successfully`
        });
        this.itemDialogVisible = false;
        this.loadItems();
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: error.error?.message || error.error?.friendlyMessage
            || `Failed to ${this.editMode ? 'update' : 'add'} item`
        });
      }
    });
  }

  deleteItem(item: StoreItem): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to delete "${item.name}"?`,
      accept: () => {
        this.storeService.deleteItem(item.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Item deleted successfully'
            });
            this.loadItems();
          },
          error: (error) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Failed to delete item: ${error.message}`
            });
          }
        });
      }
    });
  }

  redeemItem(item: StoreItem): void {
    if (!this.canGetItem(item)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Cannot get item',
        detail: this.ctaLabel(item)
      });
      return;
    }

    const type = this.acquisitionOf(item);
    let confirmMsg = `Claim "${item.name}" using ${item.price} Elite Points?`;
    if (type === 'TASKS') {
      confirmMsg = `Claim "${item.name}"? You unlocked it by completing the required tasks.`;
    } else if (type === 'POINTS_AND_TASKS') {
      confirmMsg = `Claim "${item.name}" using ${item.price} Elite Points? Required tasks are complete.`;
    }

    this.confirmationService.confirm({
      message: confirmMsg,
      accept: () => {
        const user = this.userService.getCurrentUser();
        const userId = user?.eliteId || '';

        if (!userId) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'User ID not found'
          });
          return;
        }

        this.storeService.purchaseItem(userId, item.id).subscribe({
          next: () => {
            this.loadWalletBalance(userId);
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: type === 'TASKS'
                ? 'Item claimed successfully'
                : 'Item obtained successfully'
            });
            this.loadItems();
          },
          error: (error) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: error.error?.message || error.error?.friendlyMessage
                || 'Failed to get item. Please try again later.'
            });
          }
        });
      }
    });
  }
}
