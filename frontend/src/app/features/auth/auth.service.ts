import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { LoginRequest, TokenResponse } from './auth.models';
import { TokenStorageService } from './token-storage.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http
      .post<TokenResponse>('/api/v1/auth/login', request)
      .pipe(
        tap((response) =>
          this.tokenStorage.setAccessToken(response.accessToken),
        ),
      );
  }
}