import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { vi } from 'vitest';
import { App } from './app';
import { AuthenticatedUser } from './features/auth/auth.models';
import { AuthService } from './features/auth/auth.service';

describe('App', () => {
  const authenticatedUser = signal<AuthenticatedUser | null>({
    email: 'teacher@university.com',
    authorities: ['ROLE_TEACHER'],
  });

  const isAuthenticated = signal(true);
  const isAdmin = signal(false);
  const isTeacher = signal(true);
  const isStudent = signal(false);

  const authServiceMock = {
    authenticatedUser,
    isAuthenticated,
    isAdmin,
    isTeacher,
    isStudent,
    logout: vi.fn(),
  };

  beforeEach(async () => {
    authenticatedUser.set({
      email: 'teacher@university.com',
      authorities: ['ROLE_TEACHER'],
    });

    isAuthenticated.set(true);
    isAdmin.set(false);
    isTeacher.set(true);
    isStudent.set(false);
    authServiceMock.logout.mockReset();

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render role-aware application shell', () => {
    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const navigationText = compiled.querySelector('.primary-navigation')?.textContent ?? '';

    expect(compiled.querySelector('.brand')?.textContent).toContain('University');

    expect(navigationText).toContain('My schedule');
    expect(navigationText).toContain('AI assistant');
    expect(navigationText).not.toContain('Students');

    expect(compiled.querySelector('.topbar-user')?.textContent).toContain('Teacher');
  });

  it('should clear session and navigate to login on logout', () => {
    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);

    const navigateSpy = vi.spyOn(router, 'navigateByUrl').mockResolvedValue(true);

    fixture.componentInstance['logout']();

    expect(authServiceMock.logout).toHaveBeenCalledOnce();
    expect(navigateSpy).toHaveBeenCalledWith('/login');
  });

  it('should render guest header without private sidebar', () => {
    isAuthenticated.set(false);
    authenticatedUser.set(null);
    isTeacher.set(false);

    const fixture = TestBed.createComponent(App);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.guest-header')).not.toBeNull();

    expect(compiled.querySelector('.guest-sign-in')?.textContent).toContain('Sign in');

    expect(compiled.querySelector('.sidebar')).toBeNull();
  });
});
