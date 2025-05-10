import { Component, OnInit } from '@angular/core';
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
  order: number; // To track the order of moves
}

@Component({
  selector: 'app-xo-game',
  templateUrl: './xo-game.component.html',
  styleUrls: ['./xo-game.component.scss'],
  standalone: true,
  imports: [CommonModule, ButtonModule, DialogModule, ToastModule],
  providers: [MessageService]
})
export class XoGameComponent implements OnInit {
  board: Player[] = Array(9).fill(null);
  currentPlayer: Player = 'X';
  winner: Player = null;
  gameOver: boolean = false;
  isDraw: boolean = false;
  gameMode: GameMode = 'single';
  showGameModeDialog: boolean = true;
  
  // Track moves with order
  moveHistory: Move[] = [];
  playerXMoves: Move[] = [];
  playerOMoves: Move[] = [];
  moveCount: number = 0;
  maxMovesPerPlayer: number = 3;
  
  // Game statistics
  xWins: number = 0;
  oWins: number = 0;
  draws: number = 0;
  
  // Winning combinations
  winningCombinations: number[][] = [
    [0, 1, 2], [3, 4, 5], [6, 7, 8], // Rows
    [0, 3, 6], [1, 4, 7], [2, 5, 8], // Columns
    [0, 4, 8], [2, 4, 6]             // Diagonals
  ];

  constructor(
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {}

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
    // Return if cell is already filled or game is over
    if (this.board[index] || this.gameOver) {
      return;
    }
    
    // Player makes a move
    this.moveCount++;
    
    // Add move to history
    const move: Move = {
      index: index,
      player: this.currentPlayer,
      order: this.moveCount
    };
    
    this.moveHistory.push(move);
    
    // Add move to player-specific arrays
    if (this.currentPlayer === 'X') {
      this.playerXMoves.push(move);
      
      // If player X has more than maxMovesPerPlayer moves, remove the oldest one
      if (this.playerXMoves.length > this.maxMovesPerPlayer) {
        const oldestMove = this.playerXMoves.shift(); // Remove oldest move
        if (oldestMove) {
          this.board[oldestMove.index] = null; // Clear the cell
        }
      }
    } else {
      this.playerOMoves.push(move);
      
      // If player O has more than maxMovesPerPlayer moves, remove the oldest one
      if (this.playerOMoves.length > this.maxMovesPerPlayer) {
        const oldestMove = this.playerOMoves.shift(); // Remove oldest move
        if (oldestMove) {
          this.board[oldestMove.index] = null; // Clear the cell
        }
      }
    }
    
    // Update the board with the current move
    this.board[index] = this.currentPlayer;
    
    // Check for win or draw
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
    
    // Switch player
    this.currentPlayer = this.currentPlayer === 'X' ? 'O' : 'X';
    
    // If single player mode and it's computer's turn
    if (this.gameMode === 'single' && this.currentPlayer === 'O' && !this.gameOver) {
      setTimeout(() => {
        this.computerMove();
      }, 500);
    }
  }

  computerMove(): void {
    // Simple AI for computer moves
    
    // Check if computer can win
    const winMove = this.findBestMove('O');
    if (winMove !== -1) {
      this.makeMove(winMove);
      return;
    }
    
    // Check if player can win and block
    const blockMove = this.findBestMove('X');
    if (blockMove !== -1) {
      this.makeMove(blockMove);
      return;
    }
    
    // Take center if available
    if (this.board[4] === null) {
      this.makeMove(4);
      return;
    }
    
    // Take a random available corner
    const corners = [0, 2, 6, 8];
    const availableCorners = corners.filter(i => this.board[i] === null);
    
    if (availableCorners.length > 0) {
      const randomCorner = availableCorners[Math.floor(Math.random() * availableCorners.length)];
      this.makeMove(randomCorner);
      return;
    }
    
    // Take any available side
    const sides = [1, 3, 5, 7];
    const availableSides = sides.filter(i => this.board[i] === null);
    
    if (availableSides.length > 0) {
      const randomSide = availableSides[Math.floor(Math.random() * availableSides.length)];
      this.makeMove(randomSide);
      return;
    }
  }

  findBestMove(player: Player): number {
    // Check if there's a winning move
    for (const combination of this.winningCombinations) {
      const [a, b, c] = combination;
      
      // Check if player has two in a row and third cell is empty
      if (
        this.board[a] === player && 
        this.board[b] === player && 
        this.board[c] === null
      ) {
        return c;
      }
      
      if (
        this.board[a] === player && 
        this.board[c] === player && 
        this.board[b] === null
      ) {
        return b;
      }
      
      if (
        this.board[b] === player && 
        this.board[c] === player && 
        this.board[a] === null
      ) {
        return a;
      }
    }
    
    return -1;
  }

  checkWin(): boolean {
    for (const combination of this.winningCombinations) {
      const [a, b, c] = combination;
      if (
        this.board[a] && 
        this.board[a] === this.board[b] && 
        this.board[a] === this.board[c]
      ) {
        return true;
      }
    }
    
    return false;
  }

  checkDraw(): boolean {
    // In the new game mode, a draw is not possible unless the board is full
    // and no player has won
    const filledCells = this.board.filter(cell => cell !== null).length;
    return filledCells === 9;
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
    this.router.navigate(['/auth/games']);
  }
  
  // Helper method to get cell opacity based on move order
  getCellOpacity(index: number): number {
    if (this.board[index] === null) {
      return 1; // Empty cell, full opacity
    }
    
    const playerMoves = this.board[index] === 'X' ? this.playerXMoves : this.playerOMoves;
    const move = playerMoves.find(m => m.index === index);
    
    if (!move) {
      return 1; // Should not happen, but just in case
    }
    
    // If we have maxMovesPerPlayer moves, only the oldest move should be faded
    if (playerMoves.length === this.maxMovesPerPlayer) {
      // Only the oldest move (first in array) gets reduced opacity
      if (playerMoves[0] === move) {
        return 0.4; // Oldest move gets low opacity
      }
    }
    
    return 1; // All other moves get full opacity
  }
} 