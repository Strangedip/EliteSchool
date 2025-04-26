// unauth/unauth-layout.component.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UnauthNavbarComponent } from './navbar/unauth-navbar.component';

@Component({
    selector: 'app-unauth-layout',
    standalone: true,
    imports: [RouterOutlet, UnauthNavbarComponent],
    templateUrl: './unauth-layout.component.html',
    styleUrls: ['./unauth-layout.component.scss']
})
export class UnauthLayoutComponent { }