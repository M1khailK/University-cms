import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { authGuard } from './auth.guard';
import { TokenStorageService } from './token-storage.service';

describe('authGuard', () => {
  const tokenStorage = {
    getAccessToken: vi.fn(),
  };

  const router = {
    createUrlTree: vi.fn(),
  };

  beforeEach(() => {
    vi.clearAllMocks();

    TestBed.configureTestingModule({
      providers: [
        {
          provide: TokenStorageService,
          useValue: tokenStorage,
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    });
  });

  it('allows navigation when access token exists', () => {
    tokenStorage.getAccessToken.mockReturnValue('jwt-token');

    const result = TestBed.runInInjectionContext(() => authGuard(null!, null!));

    expect(result).toBe(true);
    expect(router.createUrlTree).not.toHaveBeenCalled();
  });

  it('redirects to login when access token is missing', () => {
    const loginUrlTree = {};
    tokenStorage.getAccessToken.mockReturnValue(null);
    router.createUrlTree.mockReturnValue(loginUrlTree);

    const result = TestBed.runInInjectionContext(() => authGuard(null!, null!));

    expect(router.createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toBe(loginUrlTree);
  });
});
