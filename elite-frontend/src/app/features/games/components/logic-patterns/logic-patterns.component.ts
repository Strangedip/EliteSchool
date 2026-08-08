import { Component, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';

interface PatternRound {
  cells: (number | null)[];
  options: number[];
  answer: number;
  hint: string;
}

@Component({
  selector: 'app-logic-patterns',
  templateUrl: './logic-patterns.component.html',
  styleUrls: ['./logic-patterns.component.scss'],
  changeDetection: ChangeDetectionStrategy.Default
})
export class LogicPatternsComponent {
  score = 0;
  streak = 0;
  round = 0;
  current: PatternRound | null = null;
  feedback = 'Find the missing cell that completes the pattern.';
  locked = false;

  constructor(private router: Router) {
    this.nextRound();
  }

  backToGames(): void {
    this.router.navigate(['/games']);
  }

  nextRound(): void {
    this.round++;
    this.locked = false;
    this.current = this.buildRound();
    this.feedback = this.current.hint;
  }

  choose(option: number): void {
    if (!this.current || this.locked) return;
    this.locked = true;
    if (option === this.current.answer) {
      this.score += 10 + this.streak * 2;
      this.streak++;
      this.feedback = 'Correct — pattern locked in.';
    } else {
      this.streak = 0;
      this.feedback = `Not quite. The missing value was ${this.current.answer}.`;
    }
  }

  private buildRound(): PatternRound {
    const type = Math.floor(Math.random() * 4);
    if (type === 0) {
      // row arithmetic: each row +k
      const a = 2 + Math.floor(Math.random() * 5);
      const step = 1 + Math.floor(Math.random() * 4);
      const cells: (number | null)[] = [];
      for (let r = 0; r < 3; r++) {
        for (let c = 0; c < 3; c++) {
          cells.push(a + r * 3 * step + c * step);
        }
      }
      const answer = cells[8] as number;
      cells[8] = null;
      return {
        cells,
        answer,
        options: this.shuffleOptions(answer),
        hint: 'Each row follows the same step rule. What belongs in the empty cell?'
      };
    }
    if (type === 1) {
      // multiples
      const base = 2 + Math.floor(Math.random() * 4);
      const cells: (number | null)[] = Array.from({ length: 9 }, (_, i) => base * (i + 1));
      const answer = cells[8] as number;
      cells[8] = null;
      return {
        cells,
        answer,
        options: this.shuffleOptions(answer),
        hint: 'Numbers climb by a fixed multiple. Complete the grid.'
      };
    }
    if (type === 2) {
      // column sum style: third = first + second per row
      const cells: (number | null)[] = [];
      for (let r = 0; r < 3; r++) {
        const x = 3 + Math.floor(Math.random() * 8);
        const y = 2 + Math.floor(Math.random() * 7);
        cells.push(x, y, x + y);
      }
      const answer = cells[8] as number;
      cells[8] = null;
      return {
        cells,
        answer,
        options: this.shuffleOptions(answer),
        hint: 'In each row, the third number is the sum of the first two.'
      };
    }
    // alternating / odd positions
    const start = 1 + Math.floor(Math.random() * 6);
    const cells: (number | null)[] = Array.from({ length: 9 }, (_, i) => start + i * 2);
    const answer = cells[8] as number;
    cells[8] = null;
    return {
      cells,
      answer,
      options: this.shuffleOptions(answer),
      hint: 'Spot the constant jump between cells.'
    };
  }

  private shuffleOptions(answer: number): number[] {
    const set = new Set<number>([answer]);
    while (set.size < 4) {
      const delta = 1 + Math.floor(Math.random() * 8);
      set.add(Math.random() > 0.5 ? answer + delta : Math.max(1, answer - delta));
    }
    return [...set].sort(() => Math.random() - 0.5);
  }
}
