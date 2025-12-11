import { Routes } from '@angular/router';

export const GAMES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/games.component').then(m => m.GamesComponent),
    children: [
      { path: 'rps', loadComponent: () => import('./components/rps-game/rps-game.component').then(m => m.RpsGameComponent) },
      { path: 'xo', loadComponent: () => import('./components/xo-game/xo-game.component').then(m => m.XoGameComponent) },
      { path: '', redirectTo: 'rps', pathMatch: 'full' }
    ]
  }
];
