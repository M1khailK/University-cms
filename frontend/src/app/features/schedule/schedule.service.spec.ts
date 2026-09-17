import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ScheduleLessonResponse } from './schedule.models';
import { ScheduleService } from './schedule.service';

describe('ScheduleService', () => {
  let service: ScheduleService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ScheduleService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ScheduleService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should load the current user schedule for the requested date range', () => {
    const lessons: ScheduleLessonResponse[] = [
      {
        id: 1,
        name: 'Math lesson',
        date: '2023-04-27',
        startTime: '10:00:00',
        endTime: '12:00:00',
        subjectId: 1,
        subjectName: 'Mathematics',
        groupId: 1,
        groupName: 'Exact Sciences Group',
        teacherId: 1,
        teacherFirstName: 'Bob',
        teacherLastName: 'First',
      },
    ];

    service
      .getMySchedule('2023-04-25', '2023-04-29')
      .subscribe((response) => {
        expect(response).toEqual(lessons);
      });

    const request = httpTestingController.expectOne(
      (candidate) =>
        candidate.url === '/api/v1/schedules/me' &&
        candidate.params.get('from') === '2023-04-25' &&
        candidate.params.get('to') === '2023-04-29',
    );

    expect(request.request.method).toBe('GET');

    request.flush(lessons);
  });
}); 