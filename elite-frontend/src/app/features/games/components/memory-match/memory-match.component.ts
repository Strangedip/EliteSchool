import { Component, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';

interface MemoryCard {
  id: number;
  symbol: string;
  matched: boolean;
  flipped: boolean;
}

@Component({
  selector: 'app-memory-match',
  templateUrl: './memory-match.component.html',
  styleUrls: ['./memory-match.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MemoryMatchComponent {
  private readonly symbols = ['circle', 'square', 'triangle', 'diamond', 'star', 'hex', 'ring', 'cross'];
  cards: MemoryCard[] = [];
  moves = 0;
  matches = 0;
  locked = false;
  started = false;
  completed = false;
  private first: MemoryCard | null = null;
  private startMs = 0;
  elapsedSec = 0;
  private timerId: ReturnType<typeof setInterval> | null = null;

  constructor(private router: Router, private cdr: ChangeDetectorRef) {
    this.newGame();
  }

  backToGames(): void {
    this.clearTimer();
    this.router.navigate(['/games']);
  }

  newGame(): void {
    this.clearTimer();
    const deck = [...this.symbols, ...this.symbols]
      .map((symbol, i) => ({ id: i, symbol, matched: false, flipped: false }))
      .sort(() => Math.random() - 0.5);
    this.cards = deck;
    this.moves = 0;
    this.matches = 0;
    this.locked = false;
    this.started = false;
    this.completed = false;
    this.first = null;
    this.elapsedSec = 0;
    this.cdr.markForCheck();
  }

  flip(card: MemoryCard): void {
    if (this.locked || card.flipped || card.matched) return;

    if (!this.started) {
      this.started = true;
      this.startMs = Date.now();
      this.timerId = setInterval(() => {
        this.elapsedSec = Math.floor((Date.now() - this.startMs) / 1000);
        this.cdr.markForCheck();
      }, 250);
    }

    card.flipped = true;
    this.cdr.markForCheck();

    if (!this.first) {
      this.first = card;
      return;
    }

    this.moves++;
    this.locked = true;
    const a = this.first;
    const b = card;
    this.first = null;

    if (a.symbol === b.symbol) {
      a.matched = true;
      b.matched = true;
      this.matches++;
      this.locked = false;
      if (this.matches === this.symbols.length) {
        this.completed = true;
        this.clearTimer();
      }
      this.cdr.markForCheck();
      return;
    }

    setTimeout(() => {
      a.flipped = false;
      b.flipped = false;
      this.locked = false;
      this.cdr.markForCheck();
    }, 700);
  }

  private clearTimer(): void {
    if (this.timerId) {
      clearInterval(this.timerId);
      this.timerId = null;
    }
  }
}
