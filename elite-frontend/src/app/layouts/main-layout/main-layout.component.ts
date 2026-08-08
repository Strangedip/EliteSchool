import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet, NavigationEnd } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { Subscription, filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ThemeService } from '../../core/services/theme.service';
import { WalletService } from '../../core/services/wallet.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  roles?: string[];
}

@Component({
    selector: 'app-main-layout',
    templateUrl: './main-layout.component.html',
    styleUrls: ['./main-layout.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, RouterOutlet, ButtonModule, RouterModule]
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  isMobileMenuOpen = false;
  pageTitle = 'Dashboard';
  activeIndex = 0;
  currentUserName = '';
  roleLabel = '';
  isStudent = false;
  readonly walletBalance = signal<number | null>(null);
  private routerSubscription?: Subscription;

  /** Mission order: contribute → rewards → points → recognition; Games last (recreation). */
  private allNavItems: NavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-home', route: '/dashboard' },
    { label: 'Tasks', icon: 'pi pi-check-square', route: '/tasks' },
    { label: 'Nominations', icon: 'pi pi-star', route: '/nominations', roles: ['FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Courses', icon: 'pi pi-book', route: '/courses' },
    { label: 'Rewards', icon: 'pi pi-gift', route: '/store', roles: ['STUDENT', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Elite Points', icon: 'pi pi-star', route: '/wallet', roles: ['STUDENT', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Leaderboard', icon: 'pi pi-trophy', route: '/leaderboard', roles: ['STUDENT', 'FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Support', icon: 'pi pi-headphones', route: '/support', roles: ['STUDENT', 'FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Manage Users', icon: 'pi pi-shield', route: '/admin/users', roles: ['ADMIN', 'MANAGEMENT'] },
    { label: 'Audit', icon: 'pi pi-chart-bar', route: '/admin/audit', roles: ['ADMIN', 'MANAGEMENT'] },
    { label: 'Profile', icon: 'pi pi-user', route: '/profile' },
    { label: 'Games', icon: 'pi pi-play', route: '/games' },
    { label: 'Docs', icon: 'pi pi-book', route: '/docs' },
    { label: 'Settings', icon: 'pi pi-cog', route: '/settings' },
  ];

  navItems: NavItem[] = [];

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private walletService: WalletService,
    private cdr: ChangeDetectorRef,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.userService.getUserProfile().subscribe({
      next: () => this.refreshUserChrome(),
      error: () => this.refreshUserChrome()
    });

    this.setActiveFromUrl(this.router.url);
    this.routerSubscription = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => this.setActiveFromUrl(e.urlAfterRedirects));
  }

  private refreshUserChrome(): void {
    const user = this.userService.getCurrentUser();
    const role = user?.role?.toUpperCase() || '';
    this.currentUserName = user?.name || user?.username || '';
    this.roleLabel = this.formatRole(role);
    this.isStudent = role === 'STUDENT';
    this.navItems = this.allNavItems.filter(item => !item.roles || item.roles.includes(role));
    this.setActiveFromUrl(this.router.url);

    if (this.isStudent && user?.eliteId) {
      this.walletService.getWalletBalance(user.eliteId).subscribe({
        next: (balance) => {
          this.walletBalance.set(balance);
          this.cdr.markForCheck();
        },
        error: () => {
          this.walletBalance.set(null);
          this.cdr.markForCheck();
        }
      });
    } else {
      this.walletBalance.set(null);
    }
    this.cdr.markForCheck();
  }

  private formatRole(role: string): string {
    if (!role) return '';
    return role.charAt(0) + role.slice(1).toLowerCase();
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  private setActiveFromUrl(url: string): void {
    const index = this.navItems.findIndex(item => url.startsWith(item.route));
    if (index !== -1) {
      this.activeIndex = index;
      this.pageTitle = this.navItems[index].label;
      this.cdr.markForCheck();
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

  toggleTheme(): void {
    this.themeService.toggle();
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
