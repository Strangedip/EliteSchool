import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { TabMenuModule } from 'primeng/tabmenu';
import { ButtonModule } from 'primeng/button';
import { filter } from 'rxjs/operators';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  standalone: true,
  imports: [TabMenuModule, ButtonModule]
})
export class NavbarComponent implements OnInit {
  activeItem: MenuItem | undefined;
  items: MenuItem[] = [];

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit() {
    this.items = [
      { label: 'Dashboard', icon: 'pi pi-home', routerLink: '/auth/dashboard' },
      { label: 'Tasks', icon: 'pi pi-check-square', routerLink: '/auth/taskboard' },
      { label: 'Profile', icon: 'pi pi-user', routerLink: '/auth/profile' },
    ];

    // Listen to route changes to update the active item
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        const currentPath = this.router.url.split('/').pop();
        this.activeItem = this.items.find(item =>
          item.routerLink?.toString().includes(currentPath || '')
        );
      });

    // Set the initial active item
    const currentPath = this.router.url.split('/').pop();
    this.activeItem = this.items.find(item =>
      item.routerLink?.toString().includes(currentPath || '')
    );
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: (response) => {
        if (response.success) {
          this.router.navigate(['/unauth/login']);
        }
      },
      error: (err) => {
        console.error('Logout failed:', err);
      }
    });
  }
}