import { TestBed } from '@angular/core/testing';
import { UserAuthority } from './auth.models';
import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  beforeEach(() => {
    sessionStorage.clear();

    TestBed.configureTestingModule({});

    service = TestBed.inject(TokenStorageService);
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('should store and return access token', () => {
    const token = createAccessToken(['ROLE_TEACHER']);

    service.setAccessToken(token);

    expect(service.getAccessToken()).toBe(token);
  });

  it('should clear access token and authenticated user', () => {
    service.setAccessToken(createAccessToken(['ROLE_TEACHER']));

    service.clear();

    expect(service.getAccessToken()).toBeNull();
    expect(service.authenticatedUser()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });

  it('should update authentication state when token changes', () => {
    expect(service.isAuthenticated()).toBe(false);

    service.setAccessToken(createAccessToken(['ROLE_TEACHER']));

    expect(service.isAuthenticated()).toBe(true);

    service.clear();

    expect(service.isAuthenticated()).toBe(false);
  });

  it('should expose authenticated user and role', () => {
    service.setAccessToken(createAccessToken(['ROLE_TEACHER']));

    expect(service.authenticatedUser()).toEqual({
      email: 'teacher@university.com',
      authorities: ['ROLE_TEACHER'],
    });

    expect(service.isTeacher()).toBe(true);
    expect(service.isStudent()).toBe(false);
    expect(service.isAdmin()).toBe(false);
  });

  it('should reject malformed token as authenticated session', () => {
    service.setAccessToken('test-token');

    expect(service.getAccessToken()).toBe('test-token');
    expect(service.authenticatedUser()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });

  it('should reject expired token as authenticated session', () => {
    service.setAccessToken(createAccessToken(['ROLE_TEACHER'], Math.floor(Date.now() / 1000) - 60));

    expect(service.authenticatedUser()).toBeNull();
    expect(service.isAuthenticated()).toBe(false);
  });
});

function createAccessToken(
  authorities: UserAuthority[],
  expiresAt = Math.floor(Date.now() / 1000) + 3600,
): string {
  const header = encodeSegment({
    alg: 'HS256',
    typ: 'JWT',
  });

  const payload = encodeSegment({
    iss: 'university-cms',
    sub: 'teacher@university.com',
    iat: Math.floor(Date.now() / 1000),
    exp: expiresAt,
    authorities,
  });

  return `${header}.${payload}.test-signature`;
}

function encodeSegment(value: object): string {
  return btoa(JSON.stringify(value)).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}
