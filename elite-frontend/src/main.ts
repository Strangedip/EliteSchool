import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideHttpClient, withInterceptors, withXhr } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { ToastModule } from 'primeng/toast';
import { FormsModule } from '@angular/forms';
import { providePrimeNG } from 'primeng/config';
import { EliteAura } from './app/core/theme/elite-aura.preset';
import { httpErrorInterceptor } from './app/core/interceptors/http-error.interceptor';
import { authInterceptor } from './app/core/interceptors/auth.interceptor';

const storedTheme = localStorage.getItem('elite_theme') === 'light' ? 'light' : 'dark';
document.documentElement.setAttribute('data-theme', storedTheme);

/** Remove PrimeNG invalid-license host if present (demo / no community key configured). */
function suppressPrimeLicenseBanner(): void {
  const remove = () => document.getElementById('p-license-host')?.remove();
  remove();
  const observer = new MutationObserver(remove);
  observer.observe(document.documentElement, { childList: true, subtree: true });
}
suppressPrimeLicenseBanner();

bootstrapApplication(AppComponent, {
  providers: [
    provideZoneChangeDetection(),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withXhr(), withInterceptors([httpErrorInterceptor, authInterceptor])),
    MessageService,
    providePrimeNG({
      theme: {
        preset: EliteAura,
        options: {
          darkModeSelector: '[data-theme="dark"]',
          cssLayer: false
        }
      }
    }),
    importProvidersFrom(ToastModule, FormsModule)
  ]
}).catch(err => console.error(err));
