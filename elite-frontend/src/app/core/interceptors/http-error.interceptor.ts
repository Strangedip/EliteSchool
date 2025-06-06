import { Injectable, inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { CommonResponseDto } from '../models/common-response.model';

/**
 * HTTP Error Interceptor function for Angular 18's withInterceptors
 * Handles all HTTP errors and displays appropriate toast notifications
 */
export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const toastService = inject(ToastService);
  
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unexpected error occurred';
      
      // Check if the error response contains our CommonResponseDto format
      if (error.error && error.error.success === false) {
        const response = error.error as CommonResponseDto<any>;
        
        if (response.error) {
          errorMessage = `${response.error.errorCode}: ${response.error.errorDescription}`;
        } else if (response.message) {
          errorMessage = response.message;
        }
        
        toastService.showError(errorMessage);
      } 
      // Handle network errors or other HTTP errors
      else if (error.status === 0) {
        errorMessage = 'Network error. Please check your connection.';
        toastService.showError(errorMessage);
      } else if (error.status === 401) {
        errorMessage = 'Unauthorized. Please log in again.';
        toastService.showError(errorMessage);
      } else if (error.status === 403) {
        errorMessage = 'Access forbidden. You don\'t have permission to access this resource.';
        toastService.showError(errorMessage);
      } else if (error.status === 404) {
        errorMessage = 'Resource not found.';
        toastService.showError(errorMessage);
      } else if (error.status === 500) {
        errorMessage = 'Server error. Please try again later.';
        toastService.showError(errorMessage);
      } else {
        // For other types of errors, try to extract message from response
        if (error.error && typeof error.error === 'object') {
          // Try to extract error message from different formats
          if (error.error.message) {
            errorMessage = error.error.message;
          } else if (error.error.error && error.error.error.message) {
            errorMessage = error.error.error.message;
          } else if (error.message) {
            errorMessage = error.message;
          }
        }
        
        toastService.showError(errorMessage);
      }
      
      // Log error for debugging
      console.error('API Error:', error);
      
      // Return the error for further processing if needed
      return throwError(() => error);
    })
  );
}

/**
 * Class-based HTTP Error Interceptor for backward compatibility
 */
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private toastService: ToastService) {}
  
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'An unexpected error occurred';
        
        // Check if the error response contains our CommonResponseDto format
        if (error.error && error.error.success === false) {
          const response = error.error as CommonResponseDto<any>;
          
          if (response.error) {
            errorMessage = `${response.error.errorCode}: ${response.error.errorDescription}`;
          } else if (response.message) {
            errorMessage = response.message;
          }
          
          this.toastService.showError(errorMessage);
        } 
        // Handle network errors or other HTTP errors
        else if (error.status === 0) {
          errorMessage = 'Network error. Please check your connection.';
          this.toastService.showError(errorMessage);
        } else if (error.status === 401) {
          errorMessage = 'Unauthorized. Please log in again.';
          this.toastService.showError(errorMessage);
        } else if (error.status === 403) {
          errorMessage = 'Access forbidden. You don\'t have permission to access this resource.';
          this.toastService.showError(errorMessage);
        } else if (error.status === 404) {
          errorMessage = 'Resource not found.';
          this.toastService.showError(errorMessage);
        } else if (error.status === 500) {
          errorMessage = 'Server error. Please try again later.';
          this.toastService.showError(errorMessage);
        } else {
          // For other types of errors, try to extract message from response
          if (error.error && typeof error.error === 'object') {
            // Try to extract error message from different formats
            if (error.error.message) {
              errorMessage = error.error.message;
            } else if (error.error.error && error.error.error.message) {
              errorMessage = error.error.error.message;
            } else if (error.message) {
              errorMessage = error.message;
            }
          }
          
          this.toastService.showError(errorMessage);
        }
        
        // Log error for debugging
        console.error('API Error:', error);
        
        // Return the error for further processing if needed
        return throwError(() => error);
      })
    );
  }
} 