import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardModule } from 'primeng/card';
import { TagModule } from 'primeng/tag';
import { of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { PointsService, PointsBalanceEntry } from '../../core/services/points.service';
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
    private pointsService: PointsService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.currentUserId = this.userService.getCurrentUser()?.eliteId || '';
    this.loadLeaderboard();
  }

  loadLeaderboard(): void {
    this.loading = true;
    this.pointsService.getLeaderboard(20).pipe(
      catchError(() => of([] as PointsBalanceEntry[])),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe((wallets) => {
      const ranked = wallets.filter((w) => (w.balance ?? 0) > 0);
      this.entries = ranked.map((w, index) => ({
        rank: index + 1,
        studentId: w.studentId,
        name: w.studentName || 'Student',
        role: w.role || 'STUDENT',
        balance: w.balance
      }));

      if (this.entries.some(e => e.name === 'Student')) {
        this.userService.getAllStudents().pipe(
          catchError(() => of({ success: true, data: [] as User[], message: '' }))
        ).subscribe((users) => {
          const userMap = new Map((users.data ?? []).map(u => [u.eliteId, u]));
          this.entries = this.entries.map(entry => {
            const match = userMap.get(entry.studentId);
            return match
              ? { ...entry, name: match.name || entry.name, role: match.role || entry.role }
              : entry;
          });
        });
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
