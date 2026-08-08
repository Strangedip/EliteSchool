import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { Select } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';

import { User } from '../../core/models/user.model';
import { NewUserPayload } from '../../core/models/admin-user.model';
import { UserService } from '../../core/services/user.service';

const ROLE_OPTIONS = ['STUDENT', 'FACULTY', 'MANAGEMENT', 'ADMIN'].map(r => ({ label: r, value: r }));
const GENDER_OPTIONS = ['MALE', 'FEMALE', 'OTHER'].map(g => ({ label: g, value: g }));

@Component({
    selector: 'app-admin-users',
    templateUrl: './admin-users.component.html',
    styleUrls: ['./admin-users.component.scss'],
    imports: [
        CommonModule,
        FormsModule,
        ButtonModule,
        DialogModule,
        InputTextModule,
        PasswordModule,
        Select,
        TableModule,
        TagModule,
        TooltipModule,
        ConfirmDialogModule,
        ToastModule
    ],
    providers: [ConfirmationService, MessageService],
    changeDetection: ChangeDetectionStrategy.Eager
})
export class AdminUsersComponent implements OnInit {
  users: User[] = [];
  filteredUsers: User[] = [];
  loading = false;
  searchQuery = '';
  roleFilter: string | null = null;
  currentUserId = '';

  roleOptions = ROLE_OPTIONS;
  genderOptions = GENDER_OPTIONS;

  addUserDialogVisible = false;
  saving = false;
  newUser: NewUserPayload = this.emptyNewUser();

  constructor(
    private userService: UserService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.userService.getCurrentUser()?.eliteId || '';
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.userService.getAllUsers().subscribe({
      next: (response) => {
        this.users = response.data ?? [];
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to load users: ${error.message}` });
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    const query = this.searchQuery.toLowerCase();
    this.filteredUsers = this.users.filter(user => {
      const matchesQuery = !query ||
        user.name.toLowerCase().includes(query) ||
        user.username.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query);
      const matchesRole = !this.roleFilter || user.role === this.roleFilter;
      return matchesQuery && matchesRole;
    });
  }

  onSearch(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.applyFilters();
  }

  openAddUserDialog(): void {
    this.newUser = this.emptyNewUser();
    this.addUserDialogVisible = true;
  }

  createUser(): void {
    const username = this.newUser.username?.trim() ?? '';
    if (!/^[a-zA-Z0-9_]{3,30}$/.test(username)) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'Username must be 3–30 characters (letters, numbers, underscore)' });
      return;
    }
    if (!this.newUser.password || this.newUser.password.length < 8) {
      this.messageService.add({ severity: 'warn', summary: 'Validation', detail: 'Password must be at least 8 characters' });
      return;
    }
    this.saving = true;
    const payload: NewUserPayload = {
      ...this.newUser,
      username,
      mobileNumber: this.newUser.mobileNumber?.trim() || undefined,
      address: this.newUser.address?.trim() || undefined
    };
    this.userService.createUser(payload).subscribe({
      next: () => {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User created successfully' });
        this.addUserDialogVisible = false;
        this.saving = false;
        this.loadUsers();
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: error.error?.message || 'Failed to create user' });
        this.saving = false;
      }
    });
  }

  changeRole(user: User, role: string): void {
    if (user.role === role) return;
    this.userService.setUserRole(user.eliteId, role).subscribe({
      next: () => {
        user.role = role;
        this.messageService.add({ severity: 'success', summary: 'Success', detail: `Role updated to ${role}` });
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to update role: ${error.message}` });
      }
    });
  }

  toggleActive(user: User): void {
    const nextActive = !user.active;
    this.userService.setUserActive(user.eliteId, nextActive).subscribe({
      next: () => {
        user.active = nextActive;
        this.messageService.add({ severity: 'success', summary: 'Success', detail: `User ${nextActive ? 'activated' : 'deactivated'}` });
      },
      error: (error) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to update status: ${error.message}` });
      }
    });
  }

  deleteUser(user: User): void {
    this.confirmationService.confirm({
      message: `Are you sure you want to permanently delete "${user.name}"?`,
      accept: () => {
        this.userService.deleteUser(user.eliteId).subscribe({
          next: () => {
            this.messageService.add({ severity: 'success', summary: 'Success', detail: 'User deleted successfully' });
            this.loadUsers();
          },
          error: (error) => {
            this.messageService.add({ severity: 'error', summary: 'Error', detail: `Failed to delete user: ${error.message}` });
          }
        });
      }
    });
  }

  private emptyNewUser(): NewUserPayload {
    return { name: '', username: '', email: '', password: '', role: 'STUDENT', gender: 'MALE', age: null, mobileNumber: '', address: '' };
  }
}
