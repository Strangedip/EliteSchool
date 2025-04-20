import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthLayoutComponent } from './auth-layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { StoreComponent } from './store/store.component';
import { TaskboardComponent } from './taskboard/taskboard.component';
import { UserProfileComponent } from './user-profile/user-profile.component';



const routes: Routes = [
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'store', component: StoreComponent },
      { path: 'taskboard', component: TaskboardComponent },
      { path: 'profile', component: UserProfileComponent },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthRoutingModule { }
