import { AuthService } from './service/auth.service';
import { Component, OnInit } from '@angular/core';
import { ToastService } from './service/toast.service';
import { Router } from '@angular/router';
import { CommonResponseDto } from './model/common-response.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'elite-school';
}
