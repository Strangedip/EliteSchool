import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputTextarea } from 'primeng/inputtextarea';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

import { StoreItem } from '../../core/models/store-item.model';
import { StoreService } from '../../core/services/store.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { RewardService } from '../../core/services/reward.service';

@Component({
  selector: 'app-store',
  templateUrl: './store.component.html',
  styleUrls: ['./store.component.scss'],
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    ButtonModule,
    DialogModule,
    InputTextModule,
    InputTextarea,
    ConfirmDialogModule,
    ToastModule
  ],
  providers: [ConfirmationService, MessageService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class StoreComponent implements OnInit {
  items: StoreItem[] = [];
  filteredItems: StoreItem[] = [];
  searchQuery: string = '';
  currentUserRole: string = '';
  rewardPoints: number = 0;
  
  // Item dialog properties
  itemDialogVisible: boolean = false;
  editMode: boolean = false;
  selectedItem: StoreItem | null = null;

  constructor(
    private storeService: StoreService,
    private authService: AuthService,
    private userService: UserService,
    private rewardService: RewardService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadItems();
  }

  loadUserInfo(): void {
    const user = this.userService.getCurrentUser();
    if (user) {
      this.currentUserRole = user.role;
      if (user.role === 'STUDENT') {
        const userId = user.id || user.eliteId;
        if (userId) {
          this.loadRewardPoints(userId);
        }
      }
    }
  }

  loadRewardPoints(userId: string): void {
    this.rewardService.getWalletBalance(userId).subscribe(points => {
      this.rewardPoints = points;
    });
  }

  loadItems(): void {
    this.storeService.getAllItems().subscribe(items => {
      this.items = items;
      this.filterItems();
    });
  }

  filterItems(): void {
    this.filteredItems = this.searchQuery 
      ? this.items.filter(item => 
          item.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          item.description?.toLowerCase().includes(this.searchQuery.toLowerCase())
        )
      : this.items;
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterItems();
  }

  isAdmin(): boolean {
    return ['ADMIN', 'MANAGEMENT'].includes(this.currentUserRole);
  }

  canRedeem(item: StoreItem): boolean {
    return item.stock > 0 && this.rewardPoints >= item.price;
  }

  addItem(): void {
    this.selectedItem = {
      id: '',
      name: '',
      description: '',
      price: 0,
      stock: 0
    };
    this.editMode = false;
    this.itemDialogVisible = true;
  }

  editItem(item: StoreItem): void {
    this.selectedItem = { ...item };
    this.editMode = true;
    this.itemDialogVisible = true;
  }

  saveItem(): void {
    if (!this.selectedItem) return;

    const operation = this.editMode
      ? this.storeService.updateItem(this.selectedItem.id, this.selectedItem)
      : this.storeService.addItem(this.selectedItem);

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
          detail: `Failed to ${this.editMode ? 'update' : 'add'} item: ${error.message}`
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
    if (!this.canRedeem(item)) {
      this.messageService.add({
        severity: 'error',
        summary: 'Cannot Redeem',
        detail: 'Insufficient points or item out of stock'
      });
      return;
    }

    this.confirmationService.confirm({
      message: `Are you sure you want to redeem "${item.name}" for ${item.price} points?`,
      accept: () => {
        const user = this.userService.getCurrentUser();
        const userId = user ? (user.id || user.eliteId) : '';
        
        if (!userId) {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'User ID not found'
          });
          return;
        }
        
        this.rewardService.spendPoints(userId, item.id).subscribe({
          next: () => {
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Item redeemed successfully!'
            });
            // Reload points and items
            this.loadRewardPoints(userId);
            this.loadItems();
          },
          error: (error) => {
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Failed to redeem item: ${error.message}`
            });
          }
        });
      }
    });
  }
}