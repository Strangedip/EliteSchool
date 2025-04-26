import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { UnauthRoutingModule } from './unauth-routing.module';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { PasswordModule } from 'primeng/password';
import { SelectModule } from 'primeng/select';
import { RouterModule } from '@angular/router';
import { UnauthLayoutComponent } from './unauth-layout.component';
import { UnauthNavbarComponent } from './navbar/unauth-navbar.component';
import { ToolbarModule } from 'primeng/toolbar';
import { MenuModule } from 'primeng/menu';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@NgModule({
  declarations: [
    UnauthLayoutComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule,
    UnauthRoutingModule,
    InputTextModule,
    ButtonModule,
    PasswordModule,
    SelectModule,
    ToolbarModule,
    MenuModule,
    ToastModule,
    LoginComponent,
    RegisterComponent
  ],
  providers: [MessageService],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class UnauthModule { }
