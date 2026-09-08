import {
  HttpClient,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { authInterceptor } from './auth.interceptor';
import { TokenStorageService } from './token-storage.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let tokenStorage: TokenStorageService;

  beforeEach(() => {
    sessionStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        TokenStorageService,
        provideHttpClient(
          withInterceptors([authInterceptor]),
        ),
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

    const responsePromise = firstValueFrom(
      http.get('/api/v1/profile'),
    );

    const request = httpTesting.expectOne('/api/v1/profile');

    expect(request.request.headers.get('Authorization'))
      .toBe('Bearer test-access-token');

    request.flush({});

    await responsePromise;
  });

  it('should not add Authorization header when token is missing', async () => {
    const responsePromise = firstValueFrom(
      http.get('/api/v1/profile'),
    );

    const request = httpTesting.expectOne('/api/v1/profile');

    expect(request.request.headers.has('Authorization'))
      .toBe(false);

    request.flush({});

    await responsePromise;
  });

  it('should not expose token to external requests', async () => {
    tokenStorage.setAccessToken('test-access-token');

    const responsePromise = firstValueFrom(
      http.get('https://example.com/data'),
    );

    const request = httpTesting.expectOne(
      'https://example.com/data',
    );

    expect(request.request.headers.has('Authorization'))
      .toBe(false);

    request.flush({});

    await responsePromise;
  });
});