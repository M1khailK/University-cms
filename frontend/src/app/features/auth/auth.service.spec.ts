import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';
import { LoginRequest, TokenResponse } from './auth.models';
import { TokenStorageService } from './token-storage.service';

describe('AuthService', () => {
  let authService: AuthService;
  let httpTesting: HttpTestingController;
  let tokenStorage: TokenStorageService;

  beforeEach(() => {
    sessionStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        TokenStorageService,
        provideHttpClientTesting(),
      ],
    });

    authService = TestBed.inject(AuthService);
    httpTesting = TestBed.inject(HttpTestingController);
    tokenStorage = TestBed.inject(TokenStorageService);
  });

  afterEach(() => {
    httpTesting.verify();
    sessionStorage.clear();
  });

  it('should login and store access token', async () => {
    const loginRequest: LoginRequest = {
      email: 'alex.third@example.com',
      password: 'pass3',
    };

    const tokenResponse: TokenResponse = {
      accessToken: 'test-access-token',
      tokenType: 'Bearer',
      expiresIn: 900,
    };

    const responsePromise = firstValueFrom(
      authService.login(loginRequest),
    );

    const request = httpTesting.expectOne('/api/v1/auth/login');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(loginRequest);

    request.flush(tokenResponse);

    const response = await responsePromise;

    expect(response).toEqual(tokenResponse);
    expect(tokenStorage.getAccessToken()).toBe('test-access-token');
  });
});