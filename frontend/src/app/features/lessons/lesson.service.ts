import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { LessonCreateRequest, LessonResponse, LessonUpdateRequest } from './lesson.models';

@Injectable({
  providedIn: 'root',
})
export class LessonService {
  private readonly http = inject(HttpClient);

  getLessons(): Observable<LessonResponse[]> {
    return this.http.get<LessonResponse[]>('/api/v1/lessons');
  }

  getLesson(id: number): Observable<LessonResponse> {
    return this.http.get<LessonResponse>(`/api/v1/lessons/${id}`);
  }

  createLesson(request: LessonCreateRequest): Observable<LessonResponse> {
    return this.http.post<LessonResponse>('/api/v1/lessons', request);
  }

  updateLesson(id: number, request: LessonUpdateRequest): Observable<LessonResponse> {
    return this.http.put<LessonResponse>(`/api/v1/lessons/${id}`, request);
  }

  deleteLesson(id: number): Observable<void> {
    return this.http.delete<void>(`/api/v1/lessons/${id}`);
  }
}
