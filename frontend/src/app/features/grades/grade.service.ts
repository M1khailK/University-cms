import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { GradeCreateRequest, GradeResponse } from './grade.models';

@Injectable({
  providedIn: 'root',
})
export class GradeService {
  private readonly http = inject(HttpClient);

  getGrades(): Observable<GradeResponse[]> {
    return this.http.get<GradeResponse[]>('/api/v1/grades');
  }

  getMyGrades(): Observable<GradeResponse[]> {
    return this.http.get<GradeResponse[]>('/api/v1/grades/me');
  }

  createGrade(request: GradeCreateRequest): Observable<GradeResponse> {
    return this.http.post<GradeResponse>('/api/v1/grades', request);
  }

  deleteGrade(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/grades/${id}`);
  }
}
