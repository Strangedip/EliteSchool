import { Routes } from '@angular/router';

export const GAMES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/games.component').then(m => m.GamesComponent),
    children: [
      { path: 'xo', loadComponent: () => import('./components/xo-game/xo-game.component').then(m => m.XoGameComponent) },
      { path: 'memory', loadComponent: () => import('./components/memory-match/memory-match.component').then(m => m.MemoryMatchComponent) },
      { path: 'sequence', loadComponent: () => import('./components/sequence-span/sequence-span.component').then(m => m.SequenceSpanComponent) },
      { path: 'patterns', loadComponent: () => import('./components/logic-patterns/logic-patterns.component').then(m => m.LogicPatternsComponent) },
      { path: 'math', loadComponent: () => import('./components/mental-math/mental-math.component').then(m => m.MentalMathComponent) }
    ]
  }
];
