import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StudentsPageResponse } from './student.models';

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  private readonly http = inject(HttpClient);

  getStudents(page: number, size: number): Observable<StudentsPageResponse> {
    return this.http.get<StudentsPageResponse>('/api/v1/students', {
      params: {
        page,
        size,
      },
    });
  }
}
