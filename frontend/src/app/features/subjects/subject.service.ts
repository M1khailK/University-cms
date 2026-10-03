import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SubjectRequest, SubjectResponse } from './subject.models';

@Injectable({
  providedIn: 'root',
})
export class SubjectService {
  private readonly http = inject(HttpClient);

  getSubjects(): Observable<SubjectResponse[]> {
    return this.http.get<SubjectResponse[]>('/api/v1/subjects');
  }

  getSubject(id: number): Observable<SubjectResponse> {
    return this.http.get<SubjectResponse>(`/api/v1/subjects/${id}`);
  }

  createSubject(request: SubjectRequest): Observable<SubjectResponse> {
    return this.http.post<SubjectResponse>('/api/v1/subjects', request);
  }

  updateSubject(id: number, request: SubjectRequest): Observable<SubjectResponse> {
    return this.http.put<SubjectResponse>(`/api/v1/subjects/${id}`, request);
  }

  deleteSubject(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/subjects/${id}`);
  }
}
