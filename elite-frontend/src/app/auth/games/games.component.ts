import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';

interface GameCard {
  id: string;
  name: string;
  description: string;
  imageUrl: string;
  route: string;
}

@Component({
  selector: 'app-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss'],
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule]
})
export class GamesComponent implements OnInit {
  games: GameCard[] = [
    {
      id: 'xo',
      name: 'Tic Tac Toe',
      description: 'Classic X and O game. Challenge yourself against the computer or play with a friend.',
      imageUrl: 'assets/images/games/tic-tac-toe.svg',
      route: '/auth/games/xo'
    },
    {
      id: 'rps',
      name: 'Rock Paper Scissors',
      description: 'The classic hand game now in digital form. Can you beat the computer?',
      imageUrl: 'assets/images/games/rock-paper-scissors.svg',
      route: '/auth/games/rps'
    }
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {}

  navigateToGame(route: string): void {
    this.router.navigate([route]);
  }
} 