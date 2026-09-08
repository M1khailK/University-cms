import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenStorageService {
  private static readonly ACCESS_TOKEN_KEY = 'access_token';

  setAccessToken(token: string): void {
    sessionStorage.setItem(TokenStorageService.ACCESS_TOKEN_KEY, token);
  }

  getAccessToken(): string | null {
    return sessionStorage.getItem(TokenStorageService.ACCESS_TOKEN_KEY);
  }

  clear(): void {
    sessionStorage.removeItem(TokenStorageService.ACCESS_TOKEN_KEY);
  }
}