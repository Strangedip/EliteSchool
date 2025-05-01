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
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { InputNumberModule } from 'primeng/inputnumber';
import { PanelModule } from 'primeng/panel';
import { TableModule } from 'primeng/table';

import { StoreItem } from '../../core/models/store-item.model';
import { StoreService } from '../../core/services/store.service';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { RewardService } from '../../core/services/reward.service';
import { RewardTransaction } from '../../core/models/reward.model';

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
    ToastModule,
    CardModule,
    TagModule,
    InputNumberModule,
    PanelModule,
    TableModule
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
  
  // Image preview properties
  imagePreviewVisible: boolean = false;
  previewImageUrl: string = '';
  
  // Transaction history properties
  historyDialogVisible: boolean = false;
  transactionHistory: RewardTransaction[] = [];
  loadingHistory: boolean = false;

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
      // Normalize role to uppercase for consistent comparison
      this.currentUserRole = user.role?.toUpperCase() || '';
      
      if (this.currentUserRole === 'STUDENT') {
        const userId = user.id || user.eliteId;
        if (userId) {
          this.loadRewardPoints(userId);
        }
      }
    } else {
      // If no user found, try to fetch from API
      this.userService.getUserProfile().subscribe({
        next: (response) => {
          if (response.success && response.data) {
            this.currentUserRole = response.data.role?.toUpperCase() || '';
            
            if (this.currentUserRole === 'STUDENT') {
              const userId = response.data.id || response.data.eliteId;
              if (userId) {
                this.loadRewardPoints(userId);
              }
            }
          }
        },
        error: (error) => {
          console.error('Failed to load user profile:', error);
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: 'Failed to load user profile. Some features may be unavailable.'
          });
        }
      });
    }
  }

  loadRewardPoints(userId: string): void {
    this.rewardService.getWalletBalance(userId).subscribe({
      next: (points) => {
        this.rewardPoints = points;
      },
      error: (error) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: `Failed to load reward points: ${error.message}`
        });
      }
    });
  }

  showPurchaseHistory(): void {
    this.historyDialogVisible = true;
    this.loadTransactionHistory();
  }
  
  loadTransactionHistory(): void {
    const user = this.userService.getCurrentUser();
    if (!user) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'User information not found'
      });
      return;
    }
    
    const userId = user.id || user.eliteId;
    if (!userId) {
      this.messageService.add({
        severity: 'error',
        summary: 'Error',
        detail: 'User ID not found'
      });
      return;
    }
    
    this.loadingHistory = true;
    this.rewardService.getTransactionHistory(userId).subscribe({
      next: (transactions) => {
        this.transactionHistory = transactions;
        this.loadingHistory = false;
      },
      error: (error) => {
        console.error('Error loading transaction history:', error);
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load transaction history'
        });
        this.loadingHistory = false;
      }
    });
  }

  loadItems(): void {
    this.storeService.getAllItems().subscribe({
      next: (items) => {
        this.items = items;
        this.filterItems();
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
    this.filteredItems = this.searchQuery 
      ? this.items.filter(item => 
          item.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
          (item.description && item.description.toLowerCase().includes(this.searchQuery.toLowerCase()))
        )
      : this.items;
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterItems();
  }

  isAdmin(): boolean {
    // Normalize the comparison to handle case inconsistencies
    const role = this.currentUserRole?.toUpperCase() || '';
    return ['ADMIN', 'MANAGEMENT'].includes(role);
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
  
  previewImage(imageUrl: string): void {
    this.previewImageUrl = imageUrl;
    this.imagePreviewVisible = true;
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
        
        this.storeService.purchaseItem(userId, item.id).subscribe({
          next: (response) => {
            // Update the local reward points
            this.loadRewardPoints(userId);
            
            this.messageService.add({
              severity: 'success',
              summary: 'Success',
              detail: 'Item redeemed successfully'
            });
            
            // Reload items to reflect the updated stock
            this.loadItems();
          },
          error: (error) => {
            console.error('Error redeeming item:', error);
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: error.error?.message || 'Failed to redeem item. Please try again later.'
            });
          }
        });
      }
    });
  }
}