import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from './features/auth/auth.service';
@Component({
  imports: [RouterOutlet, RouterLink, MatToolbarModule, MatButtonModule],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected logout(): void {
    this.authService.logout();
    void this.router.navigateByUrl('/login');
  }
}
