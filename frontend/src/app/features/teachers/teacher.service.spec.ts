import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { TeacherCreateRequest, TeacherResponse, TeacherUpdateRequest } from './teacher.models';
import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TeacherService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(TeacherService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load teachers', async () => {
    const expectedResponse: TeacherResponse[] = [
      {
        id: 1,
        firstName: 'Bob',
        lastName: 'Smith',
        email: 'bob.smith@example.com',
      },
    ];

    const responsePromise = firstValueFrom(service.getTeachers());

    const request = httpTesting.expectOne('/api/v1/teachers');

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should get teacher by id', async () => {
    const expectedResponse: TeacherResponse = {
      id: 7,
      firstName: 'Arthur',
      lastName: 'Morgan',
      email: 'arthur.morgan@example.com',
    };

    const responsePromise = firstValueFrom(service.getTeacher(7));

    const request = httpTesting.expectOne('/api/v1/teachers/7');

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should create teacher', async () => {
    const requestBody: TeacherCreateRequest = {
      firstName: 'Alice',
      lastName: 'Brown',
      email: 'alice.brown@example.com',
    };

    const expectedResponse: TeacherResponse = {
      id: 2,
      ...requestBody,
    };

    const responsePromise = firstValueFrom(service.createTeacher(requestBody));

    const request = httpTesting.expectOne('/api/v1/teachers');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should update teacher', async () => {
    const requestBody: TeacherUpdateRequest = {
      firstName: 'Robert',
      lastName: 'Johnson',
      email: 'robert.johnson@example.com',
    };

    const expectedResponse: TeacherResponse = {
      id: 7,
      ...requestBody,
    };

    const responsePromise = firstValueFrom(service.updateTeacher(7, requestBody));

    const request = httpTesting.expectOne('/api/v1/teachers/7');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(requestBody);

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should deactivate teacher', async () => {
    const responsePromise = firstValueFrom(service.deactivateTeacher(7));

    const request = httpTesting.expectOne('/api/v1/teachers/7');

    expect(request.request.method).toBe('DELETE');

    request.flush(null, {
      status: 204,
      statusText: 'No Content',
    });

    await expect(responsePromise).resolves.toBeNull();
  });
});
