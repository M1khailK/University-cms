import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private readonly authService = inject(AuthService);

  protected readonly currentUser = this.authService.authenticatedUser;

  protected readonly isAdmin = this.authService.isAdmin;
  protected readonly isTeacher = this.authService.isTeacher;
  protected readonly isStudent = this.authService.isStudent;

  protected readonly displayName = computed(() => {
    const email = this.currentUser()?.email;

    if (!email) {
      return 'there';
    }

    return email
      .split('@')[0]
      .split(/[._-]+/)
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  });

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
}
