import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ChangeDetectorRef, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet, NavigationEnd } from '@angular/router';
import { Subscription, filter } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { ThemeService } from '../../core/services/theme.service';
import { PointsService } from '../../core/services/points.service';
import { BrandMarkComponent } from '../../shared/brand-mark.component';

interface NavItem {
  label: string;
  icon: string;
  route: string;
  hint: string;
  roles?: string[];
  group: 'mission' | 'recognition' | 'school' | 'personal';
}

interface NavGroup {
  id: NavItem['group'];
  label: string;
  items: NavItem[];
}

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  imports: [CommonModule, RouterOutlet, RouterModule, BrandMarkComponent]
})
export class MainLayoutComponent implements OnInit, OnDestroy {
  isMobileMenuOpen = false;
  pageTitle = 'Dashboard';
  pageHint = 'Your school workspace';
  activeRoute = '';
  currentUserName = '';
  roleLabel = '';
  roleKey = '';
  userInitials = '';
  isStudent = false;
  readonly pointsBalance = signal<number | null>(null);
  navGroups: NavGroup[] = [];
  private routerSubscription?: Subscription;

  private readonly groupMeta: { id: NavItem['group']; label: string }[] = [
    { id: 'mission', label: 'Contribute' },
    { id: 'recognition', label: 'Recognition' },
    { id: 'school', label: 'School' },
    { id: 'personal', label: 'You' }
  ];

  private readonly allNavItems: NavItem[] = [
    { label: 'Dashboard', icon: 'pi pi-home', route: '/dashboard', hint: 'Overview of what needs attention', group: 'mission' },
    { label: 'Tasks', icon: 'pi pi-check-square', route: '/tasks', hint: 'Choose work, submit, and track review', group: 'mission' },
    { label: 'Nominations', icon: 'pi pi-star', route: '/nominations', hint: 'Recognise contribution outside tasks', group: 'mission', roles: ['FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Courses', icon: 'pi pi-book', route: '/courses', hint: 'School courses and subjects', group: 'mission' },
    { label: 'Rewards', icon: 'pi pi-gift', route: '/rewards', hint: 'Materials and opportunities to claim', group: 'recognition', roles: ['STUDENT', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Elite Points', icon: 'pi pi-wallet', route: '/points', hint: 'Balance and verified credit history', group: 'recognition', roles: ['STUDENT', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Leaderboard', icon: 'pi pi-trophy', route: '/leaderboard', hint: 'See how contribution ranks', group: 'recognition', roles: ['STUDENT', 'FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Support', icon: 'pi pi-headphones', route: '/support', hint: 'Concerns and issues — not point requests', group: 'school', roles: ['STUDENT', 'FACULTY', 'ADMIN', 'MANAGEMENT'] },
    { label: 'Manage Users', icon: 'pi pi-users', route: '/admin/users', hint: 'Create and manage school accounts', group: 'school', roles: ['ADMIN', 'MANAGEMENT'] },
    { label: 'Audit', icon: 'pi pi-chart-bar', route: '/admin/audit', hint: 'Claims, credits, and accountability', group: 'school', roles: ['ADMIN', 'MANAGEMENT'] },
    { label: 'Profile', icon: 'pi pi-user', route: '/profile', hint: 'Your contribution record', group: 'personal' },
    { label: 'Games', icon: 'pi pi-play', route: '/games', hint: 'Brain training — never awards points', group: 'personal' },
    { label: 'Docs', icon: 'pi pi-file', route: '/docs', hint: 'How EliteSchool works', group: 'personal' },
    { label: 'Settings', icon: 'pi pi-cog', route: '/settings', hint: 'Preferences for your account', group: 'personal' }
  ];

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private pointsService: PointsService,
    private cdr: ChangeDetectorRef,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.refreshUserChrome();
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
    this.roleKey = role;
    this.roleLabel = this.formatRole(role);
    this.userInitials = this.buildInitials(this.currentUserName);
    this.isStudent = role === 'STUDENT';

    const visible = this.allNavItems.filter(item => !item.roles || item.roles.includes(role));
    this.navGroups = this.groupMeta
      .map(meta => ({
        id: meta.id,
        label: meta.label,
        items: visible.filter(item => item.group === meta.id)
      }))
      .filter(group => group.items.length > 0);

    this.setActiveFromUrl(this.router.url);

    if (this.isStudent && user?.eliteId) {
      this.pointsService.getPointsBalance(user.eliteId).subscribe({
        next: (balance) => {
          this.pointsBalance.set(balance);
          this.cdr.markForCheck();
        },
        error: () => {
          this.pointsBalance.set(null);
          this.cdr.markForCheck();
        }
      });
    } else {
      this.pointsBalance.set(null);
    }
    this.cdr.markForCheck();
  }

  private formatRole(role: string): string {
    if (!role) return '';
    if (role === 'ADMIN') return 'Admin';
    if (role === 'MANAGEMENT') return 'Management';
    return role.charAt(0) + role.slice(1).toLowerCase();
  }

  private buildInitials(name: string): string {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (!parts.length) return 'ES';
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  private setActiveFromUrl(url: string): void {
    const path = url.split('?')[0];
    const match = this.allNavItems
      .filter(item => path === item.route || path.startsWith(item.route + '/'))
      .sort((a, b) => b.route.length - a.route.length)[0];

    if (match) {
      this.activeRoute = match.route;
      this.pageTitle = match.label;
      this.pageHint = match.hint;
    }
    this.cdr.markForCheck();
  }

  isActive(route: string): boolean {
    return this.activeRoute === route;
  }

  onNavClick(event: Event, route: string): void {
    event.preventDefault();
    event.stopPropagation();
    this.navigateTo(route);
  }

  navigateTo(route: string): void {
    this.isMobileMenuOpen = false;
    this.cdr.markForCheck();
    void this.router.navigateByUrl(route).then((ok) => {
      if (!ok) {
        // Guard cancelled navigation — keep chrome in sync
        this.setActiveFromUrl(this.router.url);
      }
      this.cdr.markForCheck();
    });
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
