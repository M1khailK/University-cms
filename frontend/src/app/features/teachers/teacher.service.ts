import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { TeacherCreateRequest, TeacherResponse, TeacherUpdateRequest } from './teacher.models';

@Injectable({
  providedIn: 'root',
})
export class TeacherService {
  private readonly http = inject(HttpClient);

  getTeachers(): Observable<TeacherResponse[]> {
    return this.http.get<TeacherResponse[]>('/api/v1/teachers');
  }

  getTeacher(id: number): Observable<TeacherResponse> {
    return this.http.get<TeacherResponse>(`/api/v1/teachers/${id}`);
  }

  createTeacher(request: TeacherCreateRequest): Observable<TeacherResponse> {
    return this.http.post<TeacherResponse>('/api/v1/teachers', request);
  }

  updateTeacher(id: number, request: TeacherUpdateRequest): Observable<TeacherResponse> {
    return this.http.put<TeacherResponse>(`/api/v1/teachers/${id}`, request);
  }

  deactivateTeacher(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/teachers/${id}`);
  }
}
