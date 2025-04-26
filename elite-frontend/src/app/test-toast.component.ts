import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-test-toast',
  standalone: true,
  imports: [CommonModule, ButtonModule, ToastModule],
  providers: [MessageService],
  template: `
    <div style="padding: 2rem; text-align: center;">
      <h2>Toast Test Component</h2>
      <p>Click the buttons below to test different toast types</p>
      
      <div style="display: flex; gap: 1rem; justify-content: center; margin-top: 2rem;">
        <button pButton label="Success Toast" (click)="showSuccess()" style="background-color: #22c55e;"></button>
        <button pButton label="Error Toast" (click)="showError()" style="background-color: #ef4444;"></button>
        <button pButton label="Info Toast" (click)="showInfo()" style="background-color: #3b82f6;"></button>
        <button pButton label="Warning Toast" (click)="showWarning()" style="background-color: #f97316;"></button>
      </div>
      
      <p-toast position="top-right"></p-toast>
    </div>
  `
})
export class TestToastComponent {
  constructor(private messageService: MessageService) {}
  
  showSuccess() {
    this.messageService.add({
      severity: 'success',
      summary: 'Success',
      detail: 'Operation completed successfully',
      life: 3000
    });
  }
  
  showError() {
    this.messageService.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Something went wrong',
      life: 3000
    });
  }
  
  showInfo() {
    this.messageService.add({
      severity: 'info',
      summary: 'Information',
      detail: 'This is an informational message',
      life: 3000
    });
  }
  
  showWarning() {
    this.messageService.add({
      severity: 'warn',
      summary: 'Warning',
      detail: 'Please proceed with caution',
      life: 3000
    });
  }
} 