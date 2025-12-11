import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterModule, ButtonModule],
  template: `
    <div class="not-found">
      <div class="content">
        <div class="error-code">404</div>
        <h1>Page Not Found</h1>
        <p>The page you're looking for doesn't exist or has been moved.</p>
        <div class="actions">
          <a routerLink="/home" pButton label="Go Home" icon="pi pi-home" class="p-button-lg"></a>
          <a routerLink="/dashboard" pButton label="Dashboard" icon="pi pi-th-large" class="p-button-lg p-button-outlined"></a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .not-found {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #0a0f1a 0%, #050f1e 100%);
      padding: 2rem;
    }
    
    .content {
      text-align: center;
      max-width: 500px;
    }
    
    .error-code {
      font-size: clamp(6rem, 20vw, 12rem);
      font-weight: 900;
      background: linear-gradient(135deg, #00d4ff 0%, #0077b6 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      line-height: 1;
      margin-bottom: 1rem;
    }
    
    h1 {
      font-size: 2rem;
      color: #f1f5f9;
      margin: 0 0 1rem;
    }
    
    p {
      color: rgba(255, 255, 255, 0.6);
      font-size: 1.125rem;
      margin: 0 0 2rem;
    }
    
    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      flex-wrap: wrap;
      
      a {
        text-decoration: none;
      }
      
      ::ng-deep .p-button {
        background: linear-gradient(135deg, #0077b6, #00b4d8);
        border: none;
        
        &.p-button-outlined {
          background: transparent;
          border: 1px solid rgba(0, 180, 216, 0.3);
          color: #00d4ff;
        }
      }
    }
  `]
})
export class NotFoundComponent {}

