import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { PasswordChangeRequest, ProfileResponse } from './profile.models';
import { ProfileService } from './profile.service';

describe('ProfileService', () => {
  let service: ProfileService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ProfileService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ProfileService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load current user profile', async () => {
    const profile: ProfileResponse = {
      id: 2,
      firstName: 'Bob',
      lastName: 'First',
      email: 'bob.first@example.com',
      role: 'TEACHER',
      groupId: null,
      groupName: null,
    };

    const responsePromise = firstValueFrom(service.getProfile());

    const request = httpTesting.expectOne('/api/v1/profile');

    expect(request.request.method).toBe('GET');

    request.flush(profile);

    await expect(responsePromise).resolves.toEqual(profile);
  });

  it('should change current user password', async () => {
    const payload: PasswordChangeRequest = {
      oldPassword: 'old-password',
      newPassword: 'new-password',
    };

    const responsePromise = firstValueFrom(service.changePassword(payload));

    const request = httpTesting.expectOne('/api/v1/profile/password');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);

    request.flush(null);

    await expect(responsePromise).resolves.toBeNull();
  });
});
