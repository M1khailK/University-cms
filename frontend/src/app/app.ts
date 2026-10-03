import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './features/auth/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected readonly currentUser = this.authService.authenticatedUser;

  protected readonly isAdmin = this.authService.isAdmin;
  protected readonly isTeacher = this.authService.isTeacher;
  protected readonly isStudent = this.authService.isStudent;

  protected readonly menuOpen = signal(false);

  protected readonly roleLabel = computed(() => {
    if (this.isAdmin()) {
      return 'Administrator';
    }

    if (this.isTeacher()) {
      return 'Teacher';
    }

    if (this.isStudent()) {
      return 'Student';
    }

    return 'University member';
  });

  protected readonly initials = computed(() => {
    const email = this.currentUser()?.email;

    if (!email) {
      return 'UC';
    }

    return email
      .split('@')[0]
      .split(/[._-]+/)
      .map((part) => part.charAt(0))
      .join('')
      .slice(0, 2)
      .toUpperCase();
  });

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected logout(): void {
    this.authService.logout();
    this.closeMenu();
    void this.router.navigateByUrl('/login');
  }
}
