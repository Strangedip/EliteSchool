import { Component, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

type Player = 'X' | 'O' | null;
type GameMode = 'single' | 'multi';

interface Move {
  index: number;
  player: Player;
  order: number;
}

@Component({
    selector: 'app-xo-game',
    templateUrl: './xo-game.component.html',
    styleUrls: ['./xo-game.component.scss'],
    imports: [CommonModule, ButtonModule, DialogModule, ToastModule],
    changeDetection: ChangeDetectionStrategy.Eager,
    providers: [MessageService]
})
export class XoGameComponent {
  board: Player[] = Array(9).fill(null);
  currentPlayer: Player = 'X';
  winner: Player = null;
  gameOver = false;
  isDraw = false;
  gameMode: GameMode = 'single';
  showGameModeDialog = true;

  moveHistory: Move[] = [];
  playerXMoves: Move[] = [];
  playerOMoves: Move[] = [];
  moveCount = 0;
  maxMovesPerPlayer = 3;

  xWins = 0;
  oWins = 0;
  draws = 0;

  winningCombinations: number[][] = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8],
    [0, 3, 6], [1, 4, 7], [2, 5, 8],
    [0, 4, 8], [2, 4, 6]
  ];

  constructor(
    private router: Router,
    private messageService: MessageService
  ) {}

  selectGameMode(mode: GameMode): void {
    this.gameMode = mode;
    this.showGameModeDialog = false;
    this.resetGame();

    this.messageService.add({
      severity: 'info',
      summary: 'Game Mode',
      detail: mode === 'single' ? 'Playing against computer' : 'Playing with a friend',
      life: 3000
    });
  }

  makeMove(index: number): void {
    if (this.board[index] || this.gameOver) {
      return;
    }

    this.moveCount++;

    const move: Move = {
      index,
      player: this.currentPlayer,
      order: this.moveCount
    };

    this.moveHistory.push(move);

    if (this.currentPlayer === 'X') {
      this.playerXMoves.push(move);
      if (this.playerXMoves.length > this.maxMovesPerPlayer) {
        const oldestMove = this.playerXMoves.shift();
        if (oldestMove) {
          this.board[oldestMove.index] = null;
        }
      }
    } else {
      this.playerOMoves.push(move);
      if (this.playerOMoves.length > this.maxMovesPerPlayer) {
        const oldestMove = this.playerOMoves.shift();
        if (oldestMove) {
          this.board[oldestMove.index] = null;
        }
      }
    }

    this.board[index] = this.currentPlayer;

    if (this.checkWin()) {
      this.gameOver = true;
      this.winner = this.currentPlayer;
      if (this.winner === 'X') {
        this.xWins++;
      } else {
        this.oWins++;
      }
      this.messageService.add({
        severity: 'success',
        summary: 'Game Over',
        detail: `Player ${this.winner} wins!`,
        life: 3000
      });
      return;
    }

    if (this.checkDraw()) {
      this.gameOver = true;
      this.isDraw = true;
      this.draws++;
      this.messageService.add({
        severity: 'info',
        summary: 'Game Over',
        detail: 'It\'s a draw!',
        life: 3000
      });
      return;
    }

    this.currentPlayer = this.currentPlayer === 'X' ? 'O' : 'X';

    if (this.gameMode === 'single' && this.currentPlayer === 'O' && !this.gameOver) {
      setTimeout(() => this.computerMove(), 500);
    }
  }

  computerMove(): void {
    const winMove = this.findBestMove('O');
    if (winMove !== -1) {
      this.makeMove(winMove);
      return;
    }

    const blockMove = this.findBestMove('X');
    if (blockMove !== -1) {
      this.makeMove(blockMove);
      return;
    }

    if (this.board[4] === null) {
      this.makeMove(4);
      return;
    }

    const corners = [0, 2, 6, 8].filter(i => this.board[i] === null);
    if (corners.length > 0) {
      this.makeMove(corners[Math.floor(Math.random() * corners.length)]);
      return;
    }

    const available = this.board
      .map((cell, index) => cell === null ? index : -1)
      .filter(index => index !== -1);

    if (available.length > 0) {
      this.makeMove(available[Math.floor(Math.random() * available.length)]);
    }
  }

  findBestMove(player: Player): number {
    for (const combo of this.winningCombinations) {
      const [a, b, c] = combo;
      const line = [this.board[a], this.board[b], this.board[c]];
      const playerCount = line.filter(cell => cell === player).length;
      const emptyCount = line.filter(cell => cell === null).length;

      if (playerCount === 2 && emptyCount === 1) {
        if (this.board[a] === null) return a;
        if (this.board[b] === null) return b;
        if (this.board[c] === null) return c;
      }
    }
    return -1;
  }

  checkWin(): boolean {
    return this.winningCombinations.some(([a, b, c]) =>
      this.board[a] && this.board[a] === this.board[b] && this.board[a] === this.board[c]
    );
  }

  checkDraw(): boolean {
    return this.board.filter(cell => cell !== null).length === 9;
  }

  resetGame(): void {
    this.board = Array(9).fill(null);
    this.currentPlayer = 'X';
    this.winner = null;
    this.gameOver = false;
    this.isDraw = false;
    this.moveHistory = [];
    this.playerXMoves = [];
    this.playerOMoves = [];
    this.moveCount = 0;
  }

  backToGames(): void {
    this.router.navigate(['/games']);
  }

  getCellOpacity(index: number): number {
    if (this.board[index] === null) {
      return 1;
    }
    const playerMoves = this.board[index] === 'X' ? this.playerXMoves : this.playerOMoves;
    const moveIndex = playerMoves.findIndex(move => move.index === index);
    if (moveIndex === -1) return 1;
    return 1 - (playerMoves.length - 1 - moveIndex) * 0.15;
  }
}
