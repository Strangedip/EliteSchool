import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, HostListener } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-home',
  imports: [RouterModule],
  templateUrl: './home.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  navSolid = false;
  currentYear = new Date().getFullYear();
  private authSub?: { unsubscribe(): void };

  constructor(
    private authService: AuthService,
    public themeService: ThemeService
  ) {}

  toggleTheme(): void {
    this.themeService.toggle();
  }

  @HostListener('window:scroll')
  onScroll(): void {
    this.navSolid = window.scrollY > 48;
  }

  ngOnInit(): void {
    this.onScroll();
    this.authSub = this.authService.isAuthenticated$.subscribe(auth => {
      this.isLoggedIn = auth;
    });
  }

  ngOnDestroy(): void {
    this.authSub?.unsubscribe();
  }

  features = [
    {
      title: 'Track achievement',
      description: 'School tasks become a clear record of contribution — faculty verifies work so recognition is earned.'
    },
    {
      title: 'Elite Points',
      description: 'Verified effort builds Elite Points — a school measure of achievement that faculty and admins trust.'
    },
    {
      title: 'Rewards that support growth',
      description: 'Students claim materials and opportunities with points — helping every learner stay equipped.'
    },
    {
      title: 'Built for schools',
      description: 'Admins run users, courses, and rewards. Faculty set standards. Students grow through contribution.'
    }
  ];

  pillars = [
    {
      title: 'Achievement first',
      text: 'EliteSchool is a student achievement platform. Tasks, verification, and portfolios put contribution at the centre.'
    },
    {
      title: 'Verified contribution',
      text: 'Points come from approved school work reviewed by faculty or admin — real effort, not shortcuts.'
    },
    {
      title: 'Rewards that include everyone',
      text: 'Elite Points unlock school-listed materials and opportunities so students who need support are not left behind.'
    }
  ];
}
