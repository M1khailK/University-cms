import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';

import { GradeCreateRequest, GradeResponse } from './grade.models';
import { GradeService } from './grade.service';

describe('GradeService', () => {
  let service: GradeService;
  let httpTesting: HttpTestingController;

  const grade: GradeResponse = {
    id: 500,
    value: 5,
    studentId: 1,
    studentFirstName: 'Alice',
    studentLastName: 'Brown',
    studentEmail: 'alice.brown@example.com',
    lessonId: 100,
    lessonName: 'Java Basics',
    lessonDate: '2026-11-15',
    lessonStartTime: '10:00:00',
    lessonEndTime: '11:30:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GradeService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(GradeService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load grades', async () => {
    const responsePromise = firstValueFrom(service.getGrades());

    const request = httpTesting.expectOne('/api/v1/grades');

    expect(request.request.method).toBe('GET');

    request.flush([grade]);

    await expect(responsePromise).resolves.toEqual([grade]);
  });

  it('should load current student grades', async () => {
    const responsePromise = firstValueFrom(service.getMyGrades());

    const request = httpTesting.expectOne('/api/v1/grades/me');

    expect(request.request.method).toBe('GET');

    request.flush([grade]);

    await expect(responsePromise).resolves.toEqual([grade]);
  });

  it('should create grade', async () => {
    const payload: GradeCreateRequest = {
      studentId: 1,
      lessonId: 100,
      value: 5,
    };

    const responsePromise = firstValueFrom(service.createGrade(payload));

    const request = httpTesting.expectOne('/api/v1/grades');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);

    request.flush(grade);

    await expect(responsePromise).resolves.toEqual(grade);
  });

  it('should delete grade', async () => {
    const responsePromise = firstValueFrom(service.deleteGrade(500));

    const request = httpTesting.expectOne('/api/v1/grades/500');

    expect(request.request.method).toBe('DELETE');

    request.flush(null);

    await expect(responsePromise).resolves.toBeNull();
  });
});
