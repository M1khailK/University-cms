import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GroupResponse } from './group.models';

@Injectable({
  providedIn: 'root',
})
export class GroupService {
  private readonly http = inject(HttpClient);

  getGroups(): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>('/api/v1/groups');
  }
}
