import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

type Choice = 'rock' | 'paper' | 'scissors' | null;
type GameResult = 'win' | 'lose' | 'draw' | null;

@Component({
  selector: 'app-rps-game',
  templateUrl: './rps-game.component.html',
  styleUrls: ['./rps-game.component.scss'],
  standalone: true,
  imports: [CommonModule, ButtonModule, DialogModule, ToastModule],
  providers: [MessageService]
})
export class RpsGameComponent implements OnInit {
  playerChoice: Choice = null;
  computerChoice: Choice = null;
  result: GameResult = null;
  showResult: boolean = false;
  
  // Game statistics
  wins: number = 0;
  losses: number = 0;
  draws: number = 0;
  streak: number = 0;
  
  // Choice images
  choiceImages = {
    rock: 'assets/images/games/rock.svg',
    paper: 'assets/images/games/paper.svg',
    scissors: 'assets/images/games/scissors.svg'
  };
  
  // Choice display names
  choiceNames = {
    rock: 'Rock',
    paper: 'Paper',
    scissors: 'Scissors'
  };

  constructor(
    private router: Router,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {}

  makeChoice(choice: Choice): void {
    if (!choice) return;
    
    this.playerChoice = choice;
    this.computerChoice = this.getComputerChoice();
    this.result = this.determineWinner(this.playerChoice, this.computerChoice);
    
    // Update stats
    if (this.result === 'win') {
      this.wins++;
      this.streak++;
      
      // Show special message for streak
      if (this.streak >= 3) {
        this.messageService.add({
          severity: 'success',
          summary: 'Winning Streak!',
          detail: `You're on a ${this.streak} game winning streak!`,
          life: 3000
        });
      }
    } else if (this.result === 'lose') {
      this.losses++;
      this.streak = 0;
    } else {
      this.draws++;
    }
    
    // Show result
    this.showResult = true;
    
    // Show toast message
    this.messageService.add({
      severity: this.getResultSeverity(),
      summary: this.getResultSummary(),
      detail: this.getResultDetail(),
      life: 3000
    });
  }

  getComputerChoice(): Choice {
    const choices: Choice[] = ['rock', 'paper', 'scissors'];
    const randomIndex = Math.floor(Math.random() * choices.length);
    return choices[randomIndex];
  }

  determineWinner(playerChoice: Choice, computerChoice: Choice): GameResult {
    if (playerChoice === computerChoice) {
      return 'draw';
    }
    
    if (
      (playerChoice === 'rock' && computerChoice === 'scissors') ||
      (playerChoice === 'paper' && computerChoice === 'rock') ||
      (playerChoice === 'scissors' && computerChoice === 'paper')
    ) {
      return 'win';
    }
    
    return 'lose';
  }

  getResultSeverity(): string {
    switch (this.result) {
      case 'win': return 'success';
      case 'lose': return 'error';
      default: return 'info';
    }
  }

  getResultSummary(): string {
    switch (this.result) {
      case 'win': return 'You Win!';
      case 'lose': return 'You Lose!';
      default: return 'It\'s a Draw!';
    }
  }

  getResultDetail(): string {
    if (!this.playerChoice || !this.computerChoice) return '';
    
    const player = this.choiceNames[this.playerChoice];
    const computer = this.choiceNames[this.computerChoice];
    
    return `${player} vs ${computer}`;
  }

  playAgain(): void {
    this.playerChoice = null;
    this.computerChoice = null;
    this.result = null;
    this.showResult = false;
  }

  resetStats(): void {
    this.wins = 0;
    this.losses = 0;
    this.draws = 0;
    this.streak = 0;
    
    this.messageService.add({
      severity: 'info',
      summary: 'Stats Reset',
      detail: 'Game statistics have been reset',
      life: 3000
    });
  }

  backToGames(): void {
    this.router.navigate(['/auth/games']);
  }
} 