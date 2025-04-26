import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { importProvidersFrom } from '@angular/core';
import { ToastModule } from 'primeng/toast';
import { FormsModule } from '@angular/forms';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { httpErrorInterceptor } from './app/service/http-error.interceptor';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptors([httpErrorInterceptor])),
    MessageService,
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    importProvidersFrom(
      ToastModule,
      FormsModule
    )
  ]
}).catch(err => console.error(err));
