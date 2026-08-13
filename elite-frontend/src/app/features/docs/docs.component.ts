import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { BrandMarkComponent } from '../../shared/brand-mark.component';

interface DocSection {
  id: string;
  label: string;
}

interface CapabilityRow {
  name: string;
  student: boolean;
  faculty: boolean;
  admin: boolean;
  note?: string;
}

@Component({
  selector: 'app-docs',
  imports: [RouterModule, BrandMarkComponent],
  templateUrl: './docs.component.html',
  styleUrls: ['./docs.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class DocsComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  embedded = false;
  currentYear = new Date().getFullYear();
  private authSub?: { unsubscribe(): void };

  readonly sections: DocSection[] = [
    { id: 'overview', label: 'Overview' },
    { id: 'how-it-works', label: 'How it works' },
    { id: 'roles', label: 'Who uses it' },
    { id: 'modules', label: 'What you get' },
    { id: 'access', label: 'Access by role' },
    { id: 'rules', label: 'Trust rules' },
    { id: 'start', label: 'Get started' }
  ];

  readonly flow = [
    {
      title: 'Contribute',
      text: 'Students choose school tasks, complete the work, and submit notes or evidence when required.'
    },
    {
      title: 'Verify',
      text: 'Faculty or Admin reviews the submission against clear standards and approves or rejects with feedback.'
    },
    {
      title: 'Earn Elite Points',
      text: 'Approved contribution credits Elite Points — a school measure of verified effort, not a payment system.'
    },
    {
      title: 'Claim rewards',
      text: 'Students use points for Materials and Opportunities while stock and claim windows remain open.'
    }
  ];

  readonly roles = [
    {
      title: 'Students',
      text: 'Build a trusted record of contribution. Complete tasks, track Elite Points, claim school rewards, and raise support concerns when needed.'
    },
    {
      title: 'Faculty',
      text: 'Create tasks and templates, set verification standards, approve or reject work, and nominate outstanding contribution for Admin credit.'
    },
    {
      title: 'Admin / Management',
      text: 'Run users, courses, the rewards catalog, audits, and point adjustments. Only Admin can approve nomination credits to the wallet.'
    }
  ];

  readonly modules = [
    {
      title: 'Tasks',
      text: 'Choose work, mark complete, and send it for faculty review — with optional evidence, notes, and rubric standards.'
    },
    {
      title: 'Elite Points',
      text: 'A transparent balance of verified achievement. Credits come from approved tasks, Admin adjustments, or Admin-approved nominations.'
    },
    {
      title: 'Rewards',
      text: 'School-listed Materials and Opportunities. First-come stock, claim windows, and opportunity briefs keep the store fair and clear.'
    },
    {
      title: 'Nominations',
      text: 'Staff recognise contribution outside a normal task. Faculty nominate; Admin alone approves the wallet credit.'
    },
    {
      title: 'Leaderboard & profile',
      text: 'Celebrate standing and show a contribution timeline so progress is visible to the student and the school.'
    },
    {
      title: 'Courses, support & audit',
      text: 'Course visibility for the school community, a support queue for concerns only, and Admin audit for accountability.'
    },
    {
      title: 'Brain training games',
      text: 'Optional cognitive games for memory, logic, and number sense. Recreation only — they never award Elite Points.'
    }
  ];

  readonly capabilities: CapabilityRow[] = [
    { name: 'Dashboard, tasks, courses, leaderboard, profile', student: true, faculty: true, admin: true },
    { name: 'Submit / resubmit work', student: true, faculty: false, admin: false },
    { name: 'Create & verify tasks', student: false, faculty: true, admin: true },
    { name: 'Elite Points wallet & reward claims', student: true, faculty: false, admin: false },
    { name: 'Rewards catalog management', student: false, faculty: false, admin: true },
    { name: 'Support', student: true, faculty: true, admin: true },
    { name: 'Create nominations', student: false, faculty: true, admin: true },
    { name: 'Approve nomination credits', student: false, faculty: false, admin: true, note: 'Admin only' },
    { name: 'Manage users, audit, adjust points', student: false, faculty: false, admin: true }
  ];

  readonly rules = [
    'Elite Points recognise verified school effort — they are not a fintech or payment product.',
    'Staff credit points only through Elite Points adjustment or Admin-approved nomination — never through Support.',
    'Support is for concerns, issues, and misalignments only.',
    'Public signup creates student accounts. Faculty and Admin accounts are created by school management.',
    'Games never earn Elite Points.'
  ];

  constructor(
    private authService: AuthService,
    private route: ActivatedRoute,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.embedded = this.route.pathFromRoot.some(r => !!r.snapshot.data['embedded']);
    this.authSub = this.authService.isAuthenticated$.subscribe(auth => {
      this.isLoggedIn = auth;
    });
  }

  ngOnDestroy(): void {
    this.authSub?.unsubscribe();
  }

  toggleTheme(): void {
    this.themeService.toggle();
  }

  scrollTo(id: string): void {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
}
