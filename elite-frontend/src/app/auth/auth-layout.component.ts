import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { MenuModule } from 'primeng/menu';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { AuthService } from '../service/auth.service';
import { UserService } from '../service/user.service';
import { UserInfoComponent } from '../components/user-info/user-info.component';
import { Subscription } from 'rxjs';
import { User } from '../models/user.model';

@Component({
    selector: 'app-auth-layout',
    templateUrl: './auth-layout.component.html',
    styleUrls: ['./auth-layout.component.scss'],
    standalone: true,
    imports: [RouterOutlet, CommonModule, ButtonModule, MenuModule, OverlayPanelModule, UserInfoComponent, RouterModule]
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
        // Set active tab based on current route
        const currentRoute = this.router.url;
        const foundIndex = this.items.findIndex(item => currentRoute.includes(item.route));
        if (foundIndex !== -1) {
            this.activeIndex = foundIndex;
            this.pageTitle = this.items[foundIndex].label;
        }

        // Subscribe to user data changes
        this.userSubscription = this.userService.currentUser$.subscribe(user => {
            this.currentUser = user;
        });

        // Subscribe to authentication state
        this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuthenticated => {
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
}