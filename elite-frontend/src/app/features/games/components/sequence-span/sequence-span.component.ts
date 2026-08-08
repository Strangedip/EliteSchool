import { Component, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';

type Phase = 'idle' | 'showing' | 'input' | 'success' | 'fail';

@Component({
  selector: 'app-sequence-span',
  templateUrl: './sequence-span.component.html',
  styleUrls: ['./sequence-span.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SequenceSpanComponent {
  readonly digits = [1, 2, 3, 4, 5, 6, 7, 8, 9];
  sequence: number[] = [];
  input: number[] = [];
  level = 1;
  best = 0;
  phase: Phase = 'idle';
  activeFlash: number | null = null;
  status = 'Watch carefully, then repeat the sequence in order.';

  constructor(private router: Router, private cdr: ChangeDetectorRef) {
    const saved = localStorage.getItem('elite-sequence-best');
    this.best = saved ? Number(saved) || 0 : 0;
  }

  backToGames(): void {
    this.router.navigate(['/games']);
  }

  startRound(): void {
    const length = Math.min(3 + this.level - 1, 12);
    this.sequence = Array.from({ length }, () => 1 + Math.floor(Math.random() * 9));
    this.input = [];
    this.phase = 'showing';
    this.status = 'Memorise the sequence…';
    this.cdr.markForCheck();
    this.playSequence(0);
  }

  private playSequence(i: number): void {
    if (i >= this.sequence.length) {
      this.phase = 'input';
      this.activeFlash = null;
      this.status = 'Your turn — tap the digits in the same order.';
      this.cdr.markForCheck();
      return;
    }
    this.activeFlash = this.sequence[i];
    this.cdr.markForCheck();
    setTimeout(() => {
      this.activeFlash = null;
      this.cdr.markForCheck();
      setTimeout(() => this.playSequence(i + 1), 280);
    }, 650);
  }

  press(n: number): void {
    if (this.phase !== 'input') return;
    this.input.push(n);
    const idx = this.input.length - 1;
    if (this.input[idx] !== this.sequence[idx]) {
      this.phase = 'fail';
      this.status = `Missed at step ${idx + 1}. Best span: ${this.best}. Try again.`;
      this.level = 1;
      this.cdr.markForCheck();
      return;
    }
    if (this.input.length === this.sequence.length) {
      this.phase = 'success';
      this.level++;
      this.best = Math.max(this.best, this.sequence.length);
      localStorage.setItem('elite-sequence-best', String(this.best));
      this.status = `Correct! Span ${this.sequence.length}. Next round gets longer.`;
      this.cdr.markForCheck();
      return;
    }
    this.cdr.markForCheck();
  }
}
