import { computed, Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private static readonly ACCESS_TOKEN_KEY = 'access_token';
  private readonly accessToken = signal<string | null>(
    sessionStorage.getItem(TokenStorageService.ACCESS_TOKEN_KEY),
  );

  readonly isAuthenticated = computed(() => this.accessToken() !== null);
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
}
