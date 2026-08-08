import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { forkJoin } from 'rxjs';

import { WalletService, WalletBalanceEntry } from '../../core/services/wallet.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';

export interface LeaderboardEntry {
  rank: number;
  studentId: string;
  name: string;
  role: string;
  balance: number;
}

@Component({
    selector: 'app-leaderboard',
    templateUrl: './leaderboard.component.html',
    styleUrls: ['./leaderboard.component.scss'],
    changeDetection: ChangeDetectionStrategy.Eager,
    imports: [CommonModule, CardModule, TagModule]
})
export class LeaderboardComponent implements OnInit {
  entries: LeaderboardEntry[] = [];
  loading = false;
  currentUserId = '';

  constructor(
    private walletService: WalletService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.userService.getCurrentUser()?.eliteId || '';
    this.loadLeaderboard();
  }

  loadLeaderboard(): void {
    this.loading = true;
    forkJoin({
      wallets: this.walletService.getLeaderboard(20),
      users: this.userService.getAllStudents()
    }).subscribe({
      next: ({ wallets, users }) => {
        const userMap = new Map<string, User>((users.data ?? []).map(u => [u.eliteId, u]));
        this.entries = wallets
          .filter((w: WalletBalanceEntry) => w.balance > 0)
          .map((w: WalletBalanceEntry, index: number) => ({
            rank: index + 1,
            studentId: w.studentId,
            name: userMap.get(w.studentId)?.name || 'Unknown User',
            role: userMap.get(w.studentId)?.role || 'STUDENT',
            balance: w.balance
          }));
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading leaderboard:', error);
        this.loading = false;
      }
    });
  }

  getMedalClass(rank: number): string {
    if (rank === 1) return 'gold';
    if (rank === 2) return 'silver';
    if (rank === 3) return 'bronze';
    return '';
  }
}
