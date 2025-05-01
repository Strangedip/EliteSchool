import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { AuthService } from '../core/services/auth.service';
import { UserService } from '../core/services/user.service';
import { Subscription } from 'rxjs';
import { User } from '../core/models/user.model';

@Component({
    selector: 'app-auth-layout',
    templateUrl: './auth-layout.component.html',
    styleUrls: ['./auth-layout.component.scss'],
    standalone: true,
    imports: [RouterOutlet, CommonModule, ButtonModule, MenuModule, OverlayPanelModule, RouterModule]
})
export class AuthLayoutComponent implements OnInit, OnDestroy {
    isMobileMenuOpen = false;
    pageTitle = 'Dashboard';
    activeIndex = 0;
    userSubscription: Subscription | undefined;
    authSubscription: Subscription | undefined;
    currentUser: User | null = null;

    items = [
        { label: 'Dashboard', icon: 'pi pi-home', route: '/auth/dashboard' },
        { label: 'Store', icon: 'pi pi-shopping-cart', route: '/auth/store' },
        { label: 'Tasks', icon: 'pi pi-check-square', route: '/auth/taskboard' },
        { label: 'Profile', icon: 'pi pi-user', route: '/auth/profile' },
        { label: 'Settings', icon: 'pi pi-cog', route: '/auth/settings' },
    ];

    constructor(
        private router: Router, 
        private authService: AuthService,
        private userService: UserService
    ) {}

    ngOnInit() {
        // Subscribe to authentication state
        this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuthenticated => {
            console.log('Auth state changed:', isAuthenticated);
            if (!isAuthenticated) {
                this.router.navigate(['/unauth/login']);
            }
        });
    }

    ngOnDestroy() {
        if (this.authSubscription) {
            this.authSubscription.unsubscribe();
        }
        if (this.userSubscription) {
            this.userSubscription.unsubscribe();
        }
    }

    handleTabChange(index: number) {
        this.activeIndex = index;
        this.pageTitle = this.items[index].label;
        this.router.navigate([this.items[index].route]);
        
        // Close mobile menu on selection
        if (this.isMobileMenuOpen) {
            this.isMobileMenuOpen = false;
        }
    }

    toggleMobileMenu() {
        this.isMobileMenuOpen = !this.isMobileMenuOpen;
    }
    
    logout() {
        console.log('Logging out...');
        this.authService.logout().subscribe({
            next: () => {
                this.router.navigate(['/unauth/login']);
            },
            error: (err) => {
                console.error('Logout error:', err);
                // Even if there's an error, try to clear local auth state and redirect
                this.authService.clearToken();
                this.router.navigate(['/unauth/login']);
            }
        });
    }
}