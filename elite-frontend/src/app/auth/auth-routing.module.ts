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

// Note: Several components here are standalone components (RewardsComponent, TaskboardComponent, AuthLayoutComponent, StoreComponent)
const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'store', component: StoreComponent },
      { path: 'taskboard', component: TaskboardComponent },
      // Use lazy loading for rewards component
      { 
        path: 'rewards', 
        loadChildren: () => import('./rewards/rewards.module').then(m => m.RewardsModule),
        // The RewardsModule will handle loading the RewardsComponent
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
