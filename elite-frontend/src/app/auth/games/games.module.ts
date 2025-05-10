import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { GamesComponent } from './games.component';
import { XoGameComponent } from './xo-game/xo-game.component';
import { RpsGameComponent } from './rps-game/rps-game.component';
import { GamesRoutingModule } from './games-routing.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    FormsModule,
    GamesRoutingModule,
    RouterModule,
    ButtonModule,
    CardModule,
    DialogModule,
    ToastModule,
    // Import standalone components
    GamesComponent,
    XoGameComponent,
    RpsGameComponent
  ],
  providers: [MessageService]
})
export class GamesModule { } 