import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ScheduleLessonResponse } from './schedule.models';

@Injectable({
  providedIn: 'root',
})
export class ScheduleService {
  private readonly http = inject(HttpClient);

  getMySchedule(from: string, to: string): Observable<ScheduleLessonResponse[]> {
    return this.http.get<ScheduleLessonResponse[]>('/api/v1/schedules/me', {
      params: {
        from,
        to,
      },
    });
  }
}