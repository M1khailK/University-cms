import { TestBed } from '@angular/core/testing';
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
    service.setAccessToken('test-token');

    expect(service.getAccessToken()).toBe('test-token');
  });

  it('should clear access token', () => {
    service.setAccessToken('test-token');

    service.clear();

    expect(service.getAccessToken()).toBeNull();
  });

  it('should update authentication state when token changes', () => {
    expect(service.isAuthenticated()).toBe(false);

    service.setAccessToken('test-token');

    expect(service.isAuthenticated()).toBe(true);

    service.clear();

    expect(service.isAuthenticated()).toBe(false);
  });
});
