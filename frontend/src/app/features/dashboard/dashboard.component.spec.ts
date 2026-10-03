import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthenticatedUser } from '../auth/auth.models';
import { AuthService } from '../auth/auth.service';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  const authenticatedUser = signal<AuthenticatedUser | null>({
    email: 'teacher.smith@university.com',
    authorities: ['ROLE_TEACHER'],
  });

  const isAdmin = signal(false);
  const isTeacher = signal(true);
  const isStudent = signal(false);

  beforeEach(async () => {
    authenticatedUser.set({
      email: 'teacher.smith@university.com',
      authorities: ['ROLE_TEACHER'],
    });

    isAdmin.set(false);
    isTeacher.set(true);
    isStudent.set(false);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            authenticatedUser,
            isAdmin,
            isTeacher,
            isStudent,
          },
        },
      ],
    }).compileComponents();
  });

  it('should render teacher dashboard actions', () => {
    const fixture = TestBed.createComponent(DashboardComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('Welcome back, Teacher Smith');
    expect(content).toContain('Open my schedule');
    expect(content).toContain('Ask the AI assistant');
    expect(content).not.toContain('Manage students');
  });

  it('should render administrator actions', () => {
    isTeacher.set(false);
    isAdmin.set(true);

    const fixture = TestBed.createComponent(DashboardComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('Administrator');
    expect(content).toContain('Manage students');
    expect(content).not.toContain('Open my schedule');
  });
});
