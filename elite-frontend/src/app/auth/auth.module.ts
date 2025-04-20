import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TaskCreateComponent } from './taskboard/task-create/task-create.component';
import { TaskListComponent } from './taskboard/task-list/task-list.component';
import { AuthRoutingModule } from './auth-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { TaskboardComponent } from './taskboard/taskboard.component';
import { AuthLayoutComponent } from './auth-layout.component';
import { NavbarComponent } from './common/navbar/navbar.component';
import { StoreComponent } from './store/store.component';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { PasswordModule } from 'primeng/password';
import { ToolbarModule } from 'primeng/toolbar';
import { UnauthRoutingModule } from '../unauth/unauth-routing.module';

@NgModule({
  declarations: [
    TaskCreateComponent,
    TaskListComponent,
    DashboardComponent,
    UserProfileComponent,
    TaskboardComponent,
    AuthLayoutComponent,
    NavbarComponent,
    StoreComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    AuthRoutingModule,
    RouterModule,
    UnauthRoutingModule,
    InputTextModule,
    ButtonModule,
    PasswordModule,
    DropdownModule,
    ToolbarModule,
    MenuModule
  ]
})
export class AuthModule { }
