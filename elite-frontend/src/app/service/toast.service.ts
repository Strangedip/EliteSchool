import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';  // Import MessageService from PrimeNG

@Injectable({
    providedIn: 'root',
})
export class ToastService {

    constructor(private messageService: MessageService) { }

    // Show success message
    showSuccess(message: string) {
        this.messageService.add({ severity: 'success', summary: 'Success', detail: message });
    }

    // Show error message
    showError(message: string) {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: message });
    }

    // Show info message
    showInfo(message: string) {
        this.messageService.add({ severity: 'info', summary: 'Info', detail: message });
    }

    // Show warning message
    showWarning(message: string) {
        this.messageService.add({ severity: 'warn', summary: 'Warning', detail: message });
    }
}
