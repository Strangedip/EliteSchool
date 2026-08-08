import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { filter } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';

interface GameCard {
  id: string;
  name: string;
  skill: string;
  description: string;
  imageUrl: string;
  route: string;
}

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default,
  imports: [ButtonModule, RouterModule]
})
export class GamesComponent implements OnInit {
  games: GameCard[] = [
    {
      id: 'memory',
      name: 'Memory Match',
      skill: 'Working memory',
      description: 'Find every pair under time pressure. Builds visual memory and sustained attention.',
      imageUrl: 'assets/images/games/memory-match.svg',
      route: '/games/memory'
    },
    {
      id: 'sequence',
      name: 'Sequence Span',
      skill: 'Digit span',
      description: 'Watch a growing number sequence, then replay it. Classic cognitive load training.',
      imageUrl: 'assets/images/games/sequence-span.svg',
      route: '/games/sequence'
    },
    {
      id: 'patterns',
      name: 'Logic Patterns',
      skill: 'Fluid reasoning',
      description: 'Infer the hidden rule and complete the grid. Forces careful, abstract thinking.',
      imageUrl: 'assets/images/games/logic-patterns.svg',
      route: '/games/patterns'
    },
    {
      id: 'math',
      name: 'Mental Math',
      skill: 'Number fluency',
      description: 'Sixty-second arithmetic that gets harder as you succeed. Strengthens fast calculation.',
      imageUrl: 'assets/images/games/mental-math.svg',
      route: '/games/math'
    },
    {
      id: 'xo',
      name: 'Tic Tac Toe',
      skill: 'Spatial planning',
      description: 'Plan ahead against a friend or the computer. Practises foresight and strategy.',
      imageUrl: 'assets/images/games/tic-tac-toe.svg',
      route: '/games/xo'
    }
  ];

  isPlaying = false;
  isLoggedIn = false;

  private readonly playPaths = ['/games/xo', '/games/memory', '/games/sequence', '/games/patterns', '/games/math'];

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = !!this.authService.getToken();
    this.authService.isAuthenticated$.subscribe(v => this.isLoggedIn = v);
    this.syncPlayingState(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(e => this.syncPlayingState(e.urlAfterRedirects));
  }

  private syncPlayingState(url: string): void {
    this.isPlaying = this.playPaths.some(p => url.includes(p));
  }

  navigateToGame(route: string): void {
    this.router.navigate([route]);
  }
}
