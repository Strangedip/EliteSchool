import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthLayoutComponent } from './auth-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { StoreComponent } from './store/store.component';
import { TaskboardComponent } from './taskboard/taskboard.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { SettingsComponent } from './settings/settings.component';
// Import the module instead of the component directly
// import { RewardsComponent } from './rewards/rewards.component';

// Note: Several components here are standalone components (TaskboardComponent, AuthLayoutComponent, StoreComponent, WalletComponent)
const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'store', component: StoreComponent },
      { path: 'taskboard', component: TaskboardComponent },
      // Wallet route
      { 
        path: 'wallet', 
        loadChildren: () => import('./wallet/wallet.module').then(m => m.WalletModule),
      },
      // Use lazy loading for games module
      {
        path: 'games',
        loadChildren: () => import('./games/games.module').then(m => m.GamesModule),
      },
      { path: 'profile', component: UserProfileComponent },
      { path: 'settings', component: SettingsComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
