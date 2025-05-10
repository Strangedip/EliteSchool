import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { XoGameComponent } from './xo-game/xo-game.component';
import { RpsGameComponent } from './rps-game/rps-game.component';
import { GamesComponent } from './games.component';

const routes: Routes = [
  { 
    path: '', 
    component: GamesComponent 
  },
  { 
    path: 'xo', 
    component: XoGameComponent 
  },
  { 
    path: 'rps', 
    component: RpsGameComponent 
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class GamesRoutingModule { } 