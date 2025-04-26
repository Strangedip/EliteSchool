import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/service/auth.service';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
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
    
    // Set the active item based on current route
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
      }
    });
  }
} 