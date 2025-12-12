import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule, RippleModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  isLoggedIn = false;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService.isAuthenticated$.subscribe(auth => {
      this.isLoggedIn = auth;
    });
  }
  features = [
    { icon: 'pi pi-star', title: 'Earn Rewards', description: 'Complete tasks and challenges to earn Elite Points that can be redeemed for exciting rewards.' },
    { icon: 'pi pi-check-circle', title: 'Track Progress', description: 'Monitor your achievements, completed tasks, and growth journey in real-time.' },
    { icon: 'pi pi-shopping-cart', title: 'Redeem Points', description: 'Exchange your earned points for exclusive items in our Elite Store.' },
    { icon: 'pi pi-users', title: 'Community', description: 'Join a community of ambitious learners pushing each other to excellence.' }
  ];

  stats = [
    { value: '500+', label: 'Active Students' },
    { value: '1000+', label: 'Tasks Completed' },
    { value: '50+', label: 'Rewards Claimed' },
    { value: '98%', label: 'Satisfaction Rate' }
  ];

  testimonials = [
    { name: 'Priya Sharma', role: 'Computer Science Student', quote: 'EliteSchool transformed how I approach learning. The reward system keeps me motivated every day!', avatar: 'PS' },
    { name: 'Rahul Verma', role: 'Engineering Student', quote: 'The task-based system helped me build discipline. I\'ve earned amazing rewards while improving my skills.', avatar: 'RV' },
    { name: 'Ananya Patel', role: 'Management Student', quote: 'Best platform for students who want to achieve more. The Elite Store has incredible items!', avatar: 'AP' }
  ];
}

