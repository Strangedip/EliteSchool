import { Component, ChangeDetectionStrategy, ChangeDetectorRef, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

interface Problem {
  prompt: string;
  answer: number;
}

@Component({
  selector: 'app-mental-math',
  templateUrl: './mental-math.component.html',
  styleUrls: ['./mental-math.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MentalMathComponent implements OnDestroy {
  score = 0;
  streak = 0;
  correct = 0;
  attempted = 0;
  level = 1;
  timeLeft = 60;
  running = false;
  finished = false;
  problem: Problem | null = null;
  draft = '';
  message = 'Sixty seconds. Difficulty rises as you keep scoring.';
  private timerId: ReturnType<typeof setInterval> | null = null;

  constructor(private router: Router, private cdr: ChangeDetectorRef) {}

  backToGames(): void {
    this.stop();
    this.router.navigate(['/games']);
  }

  start(): void {
    this.stop();
    this.score = 0;
    this.streak = 0;
    this.correct = 0;
    this.attempted = 0;
    this.level = 1;
    this.timeLeft = 60;
    this.running = true;
    this.finished = false;
    this.draft = '';
    this.message = 'Go — answer as many as you can.';
    this.nextProblem();
    this.timerId = setInterval(() => {
      this.timeLeft--;
      if (this.timeLeft <= 0) {
        this.endRound();
      }
      this.cdr.markForCheck();
    }, 1000);
    this.cdr.markForCheck();
  }

  ngOnDestroy(): void {
    this.stop();
  }

  typeDigit(d: string): void {
    if (!this.running) return;
    if (d === '-' && this.draft.includes('-')) return;
    if (d === '-' && this.draft.length > 0) return;
    this.draft += d;
    this.cdr.markForCheck();
  }

  backspace(): void {
    if (!this.running) return;
    this.draft = this.draft.slice(0, -1);
    this.cdr.markForCheck();
  }

  submit(): void {
    if (!this.running || !this.problem || this.draft === '' || this.draft === '-') return;
    const value = Number(this.draft);
    this.attempted++;
    if (value === this.problem.answer) {
      this.correct++;
      this.streak++;
      this.score += 10 + Math.min(this.streak, 8) * 2 + this.level;
      if (this.correct % 4 === 0) this.level = Math.min(this.level + 1, 6);
      this.message = 'Correct.';
    } else {
      this.streak = 0;
      this.message = `Answer was ${this.problem.answer}. Keep going.`;
    }
    this.draft = '';
    this.nextProblem();
    this.cdr.markForCheck();
  }

  private nextProblem(): void {
    this.problem = this.makeProblem(this.level);
  }

  private makeProblem(level: number): Problem {
    const roll = Math.random();
    if (level <= 2 || roll < 0.45) {
      const a = 6 + Math.floor(Math.random() * (8 + level * 4));
      const b = 3 + Math.floor(Math.random() * (6 + level * 3));
      if (Math.random() > 0.5) return { prompt: `${a} + ${b}`, answer: a + b };
      const hi = Math.max(a, b);
      const lo = Math.min(a, b);
      return { prompt: `${hi} − ${lo}`, answer: hi - lo };
    }
    if (level <= 4 || roll < 0.8) {
      const a = 3 + Math.floor(Math.random() * (4 + level));
      const b = 3 + Math.floor(Math.random() * (4 + level));
      return { prompt: `${a} × ${b}`, answer: a * b };
    }
    const b = 2 + Math.floor(Math.random() * 8);
    const q = 2 + Math.floor(Math.random() * (4 + level));
    return { prompt: `${b * q} ÷ ${b}`, answer: q };
  }

  private endRound(): void {
    this.running = false;
    this.finished = true;
    this.message = `Time. Score ${this.score} · ${this.correct}/${this.attempted} correct.`;
    this.stop();
    this.cdr.markForCheck();
  }

  private stop(): void {
    if (this.timerId) {
      clearInterval(this.timerId);
      this.timerId = null;
    }
  }
}
