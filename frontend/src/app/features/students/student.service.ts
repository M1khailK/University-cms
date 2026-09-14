import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  StudentCreateRequest,
  StudentResponse,
  StudentsPageResponse,
  StudentUpdateRequest,
} from './student.models';
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
  getStudent(id: number): Observable<StudentResponse> {
    return this.http.get<StudentResponse>(`/api/v1/students/${id}`);
  }
  createStudent(request: StudentCreateRequest): Observable<StudentResponse> {
    return this.http.post<StudentResponse>('/api/v1/students', request);
  }
  updateStudent(id: number, request: StudentUpdateRequest): Observable<StudentResponse> {
    return this.http.put<StudentResponse>(`/api/v1/students/${id}`, request);
  }
}
