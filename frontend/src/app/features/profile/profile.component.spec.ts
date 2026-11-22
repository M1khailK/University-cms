import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';

import { ProfileResponse } from './profile.models';
import { ProfileComponent } from './profile.component';
import { ProfileService } from './profile.service';

describe('ProfileComponent', () => {
  let fixture: ComponentFixture<ProfileComponent>;
  let profileService: {
    getProfile: ReturnType<typeof vi.fn>;
  };

  const studentProfile: ProfileResponse = {
    id: 1,
    firstName: 'Alice',
    lastName: 'Brown',
    email: 'alice.brown@example.com',
    role: 'STUDENT',
    groupId: 10,
    groupName: 'AA-11',
  };

  beforeEach(async () => {
    profileService = {
      getProfile: vi.fn().mockReturnValue(of(studentProfile)),
    };

    TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        {
          provide: ProfileService,
          useValue: profileService,
        },
      ],
    });

    await TestBed.compileComponents();
  });

  it('should load and display current user profile', () => {
    fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    expect(profileService.getProfile).toHaveBeenCalledTimes(1);

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Alice Brown');
    expect(content).toContain('alice.brown@example.com');
    expect(content).toContain('Student');
    expect(content).toContain('AA-11');
  });

  it('should not show group when profile has no group', () => {
    profileService.getProfile.mockReturnValue(
      of({
        ...studentProfile,
        role: 'TEACHER',
        groupId: null,
        groupName: null,
      }),
    );

    fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Teacher');
    expect(content).not.toContain('Student group');
  });

  it('should show loading state while profile is loading', () => {
    profileService.getProfile.mockReturnValue(new Subject<ProfileResponse>());

    fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Loading profile...');
  });

  it('should show forbidden error', () => {
    profileService.getProfile.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain(
      'Your account is not allowed to access this profile.',
    );
  });

  it('should show generic loading error', () => {
    profileService.getProfile.mockReturnValue(throwError(() => new Error('Request failed')));

    fixture = TestBed.createComponent(ProfileComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Failed to load profile.');
  });
});
