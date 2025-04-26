// unauth/unauth-layout.component.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from '../components/top-navbar/top-navbar.component';

@Component({
    selector: 'app-unauth-layout',
    standalone: true,
    imports: [RouterOutlet, TopNavbarComponent],
    templateUrl: './unauth-layout.component.html',
    styleUrls: ['./unauth-layout.component.scss']
})
export class UnauthLayoutComponent { }