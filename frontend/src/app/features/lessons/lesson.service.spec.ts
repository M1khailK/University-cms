import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { LessonCreateRequest, LessonResponse, LessonUpdateRequest } from './lesson.models';
import { LessonService } from './lesson.service';

describe('LessonService', () => {
  let service: LessonService;
  let httpTesting: HttpTestingController;

  const lesson: LessonResponse = {
    id: 1,
    name: 'Java Basics',
    date: '2026-11-04',
    startTime: '15:30:00',
    endTime: '16:30:00',
    subjectId: 10,
    subjectName: 'Java',
    groupId: 20,
    groupName: 'AA-01',
    teacherId: 30,
    teacherFirstName: 'Bob',
    teacherLastName: 'Smith',
    teacherEmail: 'bob.smith@example.com',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LessonService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(LessonService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load lessons', async () => {
    const responsePromise = firstValueFrom(service.getLessons());

    const request = httpTesting.expectOne('/api/v1/lessons');

    expect(request.request.method).toBe('GET');

    request.flush([lesson]);

    await expect(responsePromise).resolves.toEqual([lesson]);
  });

  it('should load lesson by id', async () => {
    const responsePromise = firstValueFrom(service.getLesson(1));

    const request = httpTesting.expectOne('/api/v1/lessons/1');

    expect(request.request.method).toBe('GET');

    request.flush(lesson);

    await expect(responsePromise).resolves.toEqual(lesson);
  });

  it('should create lesson', async () => {
    const payload: LessonCreateRequest = {
      name: 'Java Basics',
      date: '2026-11-04',
      startTime: '15:30:00',
      endTime: '16:30:00',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    };

    const responsePromise = firstValueFrom(service.createLesson(payload));

    const request = httpTesting.expectOne('/api/v1/lessons');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);

    request.flush(lesson);

    await expect(responsePromise).resolves.toEqual(lesson);
  });

  it('should update lesson', async () => {
    const payload: LessonUpdateRequest = {
      name: 'Advanced Java',
      date: '2026-11-05',
      startTime: '16:00:00',
      endTime: '17:00:00',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    };

    const responsePromise = firstValueFrom(service.updateLesson(1, payload));

    const request = httpTesting.expectOne('/api/v1/lessons/1');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);

    request.flush({
      ...lesson,
      ...payload,
    });

    expect((await responsePromise).name).toBe('Advanced Java');
  });

  it('should delete lesson', async () => {
    const responsePromise = firstValueFrom(service.deleteLesson(1));

    const request = httpTesting.expectOne('/api/v1/lessons/1');

    expect(request.request.method).toBe('DELETE');

    request.flush(null);

    await expect(responsePromise).resolves.toBeNull();
  });
});
