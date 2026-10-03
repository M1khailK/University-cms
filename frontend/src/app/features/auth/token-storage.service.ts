import { computed, Injectable, signal } from '@angular/core';
import { AuthenticatedUser, UserAuthority } from './auth.models';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private static readonly ACCESS_TOKEN_KEY = 'access_token';

  private readonly accessToken = signal<string | null>(
    sessionStorage.getItem(TokenStorageService.ACCESS_TOKEN_KEY),
  );

  readonly authenticatedUser = computed(() => this.decodeUser(this.accessToken()));

  readonly isAuthenticated = computed(() => this.authenticatedUser() !== null);

  readonly isAdmin = computed(
    () => this.authenticatedUser()?.authorities.includes('ROLE_ADMIN') ?? false,
  );

  readonly isTeacher = computed(
    () => this.authenticatedUser()?.authorities.includes('ROLE_TEACHER') ?? false,
  );

  readonly isStudent = computed(
    () => this.authenticatedUser()?.authorities.includes('ROLE_STUDENT') ?? false,
  );

  setAccessToken(token: string): void {
    sessionStorage.setItem(TokenStorageService.ACCESS_TOKEN_KEY, token);
    this.accessToken.set(token);
  }

  getAccessToken(): string | null {
    return this.accessToken();
  }

  clear(): void {
    sessionStorage.removeItem(TokenStorageService.ACCESS_TOKEN_KEY);
    this.accessToken.set(null);
  }

  private decodeUser(token: string | null): AuthenticatedUser | null {
    if (token === null) {
      return null;
    }

    const segments = token.split('.');

    if (segments.length !== 3) {
      return null;
    }

    try {
      const base64 = segments[1].replace(/-/g, '+').replace(/_/g, '/');

      const paddedBase64 = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');

      const bytes = Uint8Array.from(atob(paddedBase64), (character) => character.charCodeAt(0));

      const payload = JSON.parse(new TextDecoder().decode(bytes)) as Record<string, unknown>;

      const email = payload['sub'];
      const expiresAt = payload['exp'];
      const rawAuthorities = payload['authorities'];

      if (
        typeof email !== 'string' ||
        email.trim().length === 0 ||
        typeof expiresAt !== 'number' ||
        expiresAt * 1000 <= Date.now() ||
        !Array.isArray(rawAuthorities) ||
        !rawAuthorities.every((authority) => this.isUserAuthority(authority))
      ) {
        return null;
      }

      return {
        email,
        authorities: rawAuthorities,
      };
    } catch {
      return null;
    }
  }

  private isUserAuthority(value: unknown): value is UserAuthority {
    return value === 'ROLE_ADMIN' || value === 'ROLE_TEACHER' || value === 'ROLE_STUDENT';
  }
}
