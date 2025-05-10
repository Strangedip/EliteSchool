import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { CUSTOM_ELEMENTS_SCHEMA, importProvidersFrom } from '@angular/core';
import { ToastModule } from 'primeng/toast';
import { FormsModule } from '@angular/forms';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { httpErrorInterceptor } from './app/core/interceptors/http-error.interceptor';
import { authInterceptor } from './app/core/interceptors/auth.interceptor';
import { CoreModule } from './app/core/core.module';

// Import compiler for JIT compilation support during transition to standalone components
import '@angular/compiler';

bootstrapApplication(AppComponent, {
  providers: [
    // Core functionality
    importProvidersFrom(CoreModule),
    
    // Routing
    provideRouter(routes),
    
    // Animations
    provideAnimations(),
    
    // HTTP with interceptors
    provideHttpClient(withInterceptors([httpErrorInterceptor, authInterceptor])),
    
    // PrimeNG
    MessageService,
    providePrimeNG({
      theme: {
        preset: Aura
      }
    }),
    
    // Additional modules
    importProvidersFrom(
      ToastModule,
      FormsModule
    )
  ]
}).catch(err => console.error(err));
