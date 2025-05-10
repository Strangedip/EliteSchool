import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpInterceptorFn
} from '@angular/common/http';
import { Observable } from 'rxjs';

// Helper function to get token without using AuthService (to avoid circular dependency)
const getToken = (): string | null => {
  return localStorage.getItem('Authorization') || localStorage.getItem('token');
};

/**
 * Auth interceptor function - for use with Angular 18's withInterceptors
 */
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  // Skip authentication for auth endpoints
  if (request.url.includes('/auth/login') || 
      request.url.includes('/auth/register') ||
      request.url.includes('/auth/signup')) {
    return next(request);
  }
  
  // Get token directly from localStorage
  const token = getToken();
  
  if (token && typeof token === 'string' && token.trim() !== '') {
    // Clone request and add authorization header
    const authRequest = request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    return next(authRequest);
  }
  
  // No token available, just pass the original request
  return next(request);
};

/**
 * Class-based interceptor for backward compatibility
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Skip authentication for auth endpoints
    if (request.url.includes('/auth/login') || 
        request.url.includes('/auth/register') ||
        request.url.includes('/auth/signup')) {
      return next.handle(request);
    }
    
    // Get token directly from localStorage
    const token = getToken();
    
    if (token && typeof token === 'string' && token.trim() !== '') {
      // Clone request and add authorization header
      const authRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      
      return next.handle(authRequest);
    }
    
    // No token available, just pass the original request
    return next.handle(request);
  }
} 