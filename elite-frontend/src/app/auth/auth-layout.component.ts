import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MenuItem } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { AuthService } from '../service/auth.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'app-auth-layout',
    standalone: true,
    imports: [RouterOutlet, CommonModule, ButtonModule, MenuModule, OverlayPanelModule],
    templateUrl: './auth-layout.component.html',
    styleUrls: ['./auth-layout.component.scss']
})
export class AuthLayoutComponent implements OnInit, OnDestroy {
    activeIndex: number = 0;
    items: MenuItem[] = [];
    userMenuItems: MenuItem[] = [];
    pageTitle: string = 'Dashboard';
    userName: string = 'Student';
    isMobileMenuOpen: boolean = false;
    private authSubscription: Subscription | undefined;

    constructor(private router: Router, private authService: AuthService) {}

    ngOnInit() {
        this.items = [
            { label: 'Dashboard', icon: 'pi pi-home', routerLink: '/auth/dashboard' },
            { label: 'Tasks', icon: 'pi pi-check-square', routerLink: '/auth/taskboard' },
            { label: 'Settings', icon: 'pi pi-cog', routerLink: '/auth/settings' },
        ];
        
        this.userMenuItems = [
            { label: 'Profile', icon: 'pi pi-user', command: () => this.router.navigate(['/auth/profile']) },
            { label: 'Logout', icon: 'pi pi-sign-out', command: () => this.logout() }
        ];
        
        // Set the active tab based on current route
        const currentPath = this.router.url.split('/').pop();
        const index = this.items.findIndex(item => 
            item.routerLink?.toString().includes(currentPath || '')
        );
        this.activeIndex = index !== -1 ? index : 0;
        this.updatePageTitle();

        // Check authentication status
        this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuthenticated => {
            if (!isAuthenticated) {
                this.authService.navigateToLogin();
            }
        });
    }

    ngOnDestroy() {
        if (this.authSubscription) {
            this.authSubscription.unsubscribe();
        }
    }

    handleTabChange(index: number) {
        this.activeIndex = index;
        const route = this.items[this.activeIndex].routerLink;
        if (route) {
            this.router.navigate([route]);
            this.updatePageTitle();
            // Close mobile menu when a tab is selected
            this.isMobileMenuOpen = false;
        }
    }

    updatePageTitle() {
        if (this.items[this.activeIndex]) {
            this.pageTitle = this.items[this.activeIndex].label || 'Dashboard';
        }
    }

    toggleMobileMenu(): void {
        this.isMobileMenuOpen = !this.isMobileMenuOpen;
    }

    logout(): void {
        this.authService.logout().subscribe({
            next: (response) => {
                if (response.success) {
                    this.authService.navigateToLogin();
                }
            }
        });
    }
}