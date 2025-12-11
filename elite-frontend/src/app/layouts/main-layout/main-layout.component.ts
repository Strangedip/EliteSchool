import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet, NavigationEnd } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { Subscription, filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  standalone: true,
  imports: [RouterOutlet, CommonModule, ButtonModule, RouterModule]
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  isMobileMenuOpen = false;
  pageTitle = 'Dashboard';
  activeIndex = 0;
  private routerSubscription?: Subscription;

  navItems: NavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-home', route: '/dashboard' },
    { label: 'Tasks', icon: 'pi pi-check-square', route: '/tasks' },
    { label: 'Store', icon: 'pi pi-shopping-cart', route: '/store' },
    { label: 'Wallet', icon: 'pi pi-wallet', route: '/wallet' },
    { label: 'Profile', icon: 'pi pi-user', route: '/profile' },
    { label: 'Games', icon: 'pi pi-play', route: '/games' },
    { label: 'Settings', icon: 'pi pi-cog', route: '/settings' },
  ];

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.setActiveFromUrl(this.router.url);
    this.routerSubscription = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => this.setActiveFromUrl(e.urlAfterRedirects));
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  private setActiveFromUrl(url: string): void {
    const index = this.navItems.findIndex(item => url.startsWith(item.route));
    if (index !== -1) {
      this.activeIndex = index;
      this.pageTitle = this.navItems[index].label;
    }
  }

  navigateTo(index: number): void {
    this.activeIndex = index;
    this.pageTitle = this.navItems[index].label;
    this.router.navigate([this.navItems[index].route]);
    this.isMobileMenuOpen = false;
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => {
        this.authService.clearToken();
        this.router.navigate(['/login']);
      }
    });
  }
}

