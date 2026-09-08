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
});