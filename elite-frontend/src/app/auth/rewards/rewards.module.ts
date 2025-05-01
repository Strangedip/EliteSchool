import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RewardsComponent } from './rewards.component';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';

const routes: Routes = [
  { path: '', component: RewardsComponent } // Default route points to RewardsComponent
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(routes), // Register child routes
    FormsModule,
    ButtonModule,
    TableModule,
    RewardsComponent // Import standalone component
  ],
  exports: [
    RewardsComponent
  ]
})
export class RewardsModule { } 