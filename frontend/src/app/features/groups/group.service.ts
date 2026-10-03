import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { GroupRequest, GroupResponse, GroupStudentResponse } from './group.models';

@Injectable({
  providedIn: 'root',
})
export class GroupService {
  private readonly http = inject(HttpClient);

  getGroups(): Observable<GroupResponse[]> {
    return this.http.get<GroupResponse[]>('/api/v1/groups');
  }

  getGroup(id: number): Observable<GroupResponse> {
    return this.http.get<GroupResponse>(`/api/v1/groups/${id}`);
  }

  getGroupStudents(id: number): Observable<GroupStudentResponse[]> {
    return this.http.get<GroupStudentResponse[]>(`/api/v1/groups/${id}/students`);
  }

  createGroup(request: GroupRequest): Observable<GroupResponse> {
    return this.http.post<GroupResponse>('/api/v1/groups', request);
  }

  updateGroup(id: number, request: GroupRequest): Observable<GroupResponse> {
    return this.http.put<GroupResponse>(`/api/v1/groups/${id}`, request);
  }

  deleteGroup(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/groups/${id}`);
  }
}
