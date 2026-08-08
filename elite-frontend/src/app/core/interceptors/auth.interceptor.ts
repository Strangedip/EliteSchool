import { HttpInterceptorFn } from '@angular/common/http';

const getToken = (): string | null =>
  localStorage.getItem('Authorization') || localStorage.getItem('token');

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  if (request.url.includes('/login') ||
      request.url.includes('/register') ||
      request.url.includes('/signup')) {
    return next(request);
  }

  const token = getToken();
  if (token && token.trim() !== '') {
    return next(request.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    }));
  }

  return next(request);
};
