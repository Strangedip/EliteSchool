import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

/**
 * Service for displaying toast notifications throughout the application
 */
@Injectable({
    providedIn: 'root',
})
export class ToastService {

    constructor(private messageService: MessageService) { }

    /**
     * Show success toast message
     */
    showSuccess(message: string, title: string = 'Success', options: any = {}) {
        this.messageService.add({ 
            severity: 'success', 
            summary: title, 
            detail: message,
            life: options.life || 2500,
            ...options
        });
    }

    /**
     * Show error toast message
     */
    showError(message: string, title: string = 'Error', options: any = {}) {
        this.messageService.add({ 
            severity: 'error', 
            summary: title, 
            detail: message,
            life: options.life || 3500,
            ...options
        });
    }

    /**
     * Show info toast message
     */
    showInfo(message: string, title: string = 'Info', options: any = {}) {
        this.messageService.add({ 
            severity: 'info', 
            summary: title, 
            detail: message,
            life: options.life || 2000,
            ...options
        });
    }

    /**
     * Show warning toast message
     */
    showWarning(message: string, title: string = 'Warning', options: any = {}) {
        this.messageService.add({ 
            severity: 'warn', 
            summary: title, 
            detail: message,
            life: options.life || 3000,
            ...options
        });
    }

    /**
     * Clear all toast messages
     */
    clearToasts(key?: string) {
        this.messageService.clear(key);
    }
} 