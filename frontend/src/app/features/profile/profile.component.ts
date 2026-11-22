import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { finalize } from 'rxjs';

import { ProfileResponse } from './profile.models';
import { ProfileService } from './profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss',
})
export class ProfileComponent implements OnInit {
  private readonly profileService = inject(ProfileService);

  protected readonly profile = signal<ProfileResponse | null>(null);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly initials = computed(() => {
    const profile = this.profile();

    if (!profile) {
      return 'U';
    }

    return `${profile.firstName.charAt(0)}${profile.lastName.charAt(0)}`.toUpperCase();
  });

  protected readonly roleLabel = computed(() => {
    const role = this.profile()?.role;

    if (role === 'TEACHER') {
      return 'Teacher';
    }

    if (role === 'STUDENT') {
      return 'Student';
    }

    return role ?? '';
  });

  ngOnInit(): void {
    this.loadProfile();
  }

  private loadProfile(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.profileService
      .getProfile()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (profile) => {
          this.profile.set(profile);
        },
        error: (error: unknown) => {
          this.profile.set(null);
          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 403) {
      return 'Your account is not allowed to access this profile.';
    }

    return 'Failed to load profile.';
  }
}
