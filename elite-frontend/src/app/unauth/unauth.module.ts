import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { UnauthRoutingModule } from './unauth-routing.module';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { DropdownModule } from 'primeng/dropdown';
import {  RouterModule } from '@angular/router';
import { UnauthLayoutComponent } from './unauth-layout.component';
import { UnauthNavbarComponent } from './unauth-navbar/unauth-navbar.component';
import { ToolbarModule } from 'primeng/toolbar';
import { MenuModule } from 'primeng/menu';


@NgModule({
  declarations: [
    LoginComponent,
    RegisterComponent,
    UnauthLayoutComponent,
    UnauthNavbarComponent
  ],
  imports: [
    RouterModule,
    CommonModule,
    FormsModule,
    UnauthRoutingModule,
    InputTextModule,
    ButtonModule,
    PasswordModule,
    DropdownModule,
    ToolbarModule,
    MenuModule
  ]
})
export class UnauthModule { }
