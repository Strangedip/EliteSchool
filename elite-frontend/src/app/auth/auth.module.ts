import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TaskCreateComponent } from './taskboard/task-create/task-create.component';
import { AuthRoutingModule } from './auth-routing.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserProfileComponent } from './user-profile/user-profile.component';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { PasswordModule } from 'primeng/password';
import { ToolbarModule } from 'primeng/toolbar';
import { TabMenuModule } from 'primeng/tabmenu';
import { UnauthRoutingModule } from '../unauth/unauth-routing.module';
import { AuthLayoutComponent } from './auth-layout.component';
import { AuthGuard } from './auth.guard';

/**
 * Note: Several components in this module are now standalone components:
 * - AuthLayoutComponent
 * - TaskboardComponent
 * - RewardsComponent
 * - StoreComponent
 * 
 * These components should not be imported directly in this module.
 * They are loaded directly by the router.
 */
@NgModule({
  declarations: [
    TaskCreateComponent,
    DashboardComponent,
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
    MenuModule,
    TabMenuModule,
    // Note: AuthLayoutComponent is a standalone component and should be imported through the router
  ],
  providers: [
    AuthGuard
  ]
})
export class AuthModule { }
