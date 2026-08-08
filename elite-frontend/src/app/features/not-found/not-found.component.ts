import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';

@Component({
    selector: 'app-not-found',
    imports: [RouterModule, ButtonModule],
    template: `
    <div class="not-found">
      <div class="content">
        <div class="error-code">404</div>
        <h1>Page Not Found</h1>
        <p>The page you're looking for doesn't exist or has been moved.</p>
        <div class="actions">
          <p-button label="Go Home" icon="pi pi-home" styleClass="p-button-lg" (onClick)="goHome()"></p-button>
          <p-button label="Dashboard" icon="pi pi-th-large" [outlined]="true" styleClass="p-button-lg" (onClick)="goDashboard()"></p-button>
        </div>
      </div>
    </div>
  `,
    changeDetection: ChangeDetectionStrategy.Eager,
    styles: [`
    .not-found {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--surface-ground);
      padding: 2rem;
    }

    .content {
      text-align: center;
      max-width: 500px;
    }

    .error-code {
      font-size: clamp(6rem, 20vw, 12rem);
      font-weight: 900;
      background: var(--gradient-brand);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      line-height: 1;
      margin-bottom: 1rem;
    }

    h1 {
      font-size: 2rem;
      color: var(--text-color);
      margin: 0 0 1rem;
    }

    p {
      color: var(--text-color-secondary);
      font-size: 1.125rem;
      margin: 0 0 2rem;
    }

    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
      flex-wrap: wrap;
    }
  `]
})
export class NotFoundComponent {
  private readonly router = inject(Router);

  goHome(): void {
    void this.router.navigate(['/home']);
  }

  goDashboard(): void {
    void this.router.navigate(['/dashboard']);
  }
}
