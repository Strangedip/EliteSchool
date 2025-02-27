import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: 'auth', loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule) },
  { path: 'unauth', loadChildren: () => import('./unauth/unauth.module').then(m => m.UnauthModule) },
  { path: '', redirectTo: 'unauth/login', pathMatch: 'full' },  // Default route
  { path: '**', redirectTo: 'unauth/login' }  // Wildcard route for unknown paths
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
