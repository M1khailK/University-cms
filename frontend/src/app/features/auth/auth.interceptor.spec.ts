import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { authInterceptor } from './auth.interceptor';
import { TokenStorageService } from './token-storage.service';
import { Router } from '@angular/router';
import { vi } from 'vitest';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let tokenStorage: TokenStorageService;
  const router = {
    navigateByUrl: vi.fn(),
  };
  beforeEach(() => {
    vi.clearAllMocks();
    sessionStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        {
          provide: Router,
          useValue: router,
        },
        TokenStorageService,
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    tokenStorage = TestBed.inject(TokenStorageService);
  });

  afterEach(() => {
    httpTesting.verify();
    sessionStorage.clear();
  });

  it('should add Bearer token to API requests', async () => {
    tokenStorage.setAccessToken('test-access-token');

    const responsePromise = firstValueFrom(http.get('/api/v1/profile'));

    const request = httpTesting.expectOne('/api/v1/profile');

    expect(request.request.headers.get('Authorization')).toBe('Bearer test-access-token');

    request.flush({});

    await responsePromise;
  });

  it('should not add Authorization header when token is missing', async () => {
    const responsePromise = firstValueFrom(http.get('/api/v1/profile'));

    const request = httpTesting.expectOne('/api/v1/profile');

    expect(request.request.headers.has('Authorization')).toBe(false);

    request.flush({});

    await responsePromise;
  });

  it('should not expose token to external requests', async () => {
    tokenStorage.setAccessToken('test-access-token');

    const responsePromise = firstValueFrom(http.get('https://example.com/data'));

    const request = httpTesting.expectOne('https://example.com/data');

    expect(request.request.headers.has('Authorization')).toBe(false);

    request.flush({});

    await responsePromise;
  });

  it('should clear session and redirect to login on API 401', async () => {
    tokenStorage.setAccessToken('expired-token');

    const responsePromise = firstValueFrom(http.get('/api/v1/profile'));
    const rejection = expect(responsePromise).rejects.toMatchObject({
      status: 401,
    });

    const request = httpTesting.expectOne('/api/v1/profile');

    request.flush(
      {},
      {
        status: 401,
        statusText: 'Unauthorized',
      },
    );

    await rejection;

    expect(tokenStorage.getAccessToken()).toBeNull();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('should keep session on API 403', async () => {
    tokenStorage.setAccessToken('valid-token');

    const responsePromise = firstValueFrom(http.get('/api/v1/students'));
    const rejection = expect(responsePromise).rejects.toMatchObject({
      status: 403,
    });

    const request = httpTesting.expectOne('/api/v1/students');

    request.flush(
      {},
      {
        status: 403,
        statusText: 'Forbidden',
      },
    );

    await rejection;

    expect(tokenStorage.getAccessToken()).toBe('valid-token');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should not add Authorization header to login request', async () => {
    tokenStorage.setAccessToken('stale-token');

    const responsePromise = firstValueFrom(
      http.post('/api/v1/auth/login', {
        email: 'test@example.com',
        password: 'password',
      }),
    );

    const request = httpTesting.expectOne('/api/v1/auth/login');

    expect(request.request.headers.has('Authorization')).toBe(false);

    request.flush({
      accessToken: 'new-token',
      tokenType: 'Bearer',
      expiresIn: 900,
    });

    await responsePromise;
  });
});
