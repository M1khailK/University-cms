import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PasswordChangeRequest, ProfileResponse } from './profile.models';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  private readonly http = inject(HttpClient);

  getProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>('/api/v1/profile');
  }

  changePassword(request: PasswordChangeRequest): Observable<void> {
    return this.http.put<void>('/api/v1/profile/password', request);
  }
}
