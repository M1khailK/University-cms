import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { StudentService } from './student.service';
import {
  StudentCreateRequest,
  StudentResponse,
  StudentsPageResponse,
  StudentUpdateRequest,
} from './student.models';

describe('StudentService', () => {
  let service: StudentService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StudentService, provideHttpClientTesting()],
    });

    service = TestBed.inject(StudentService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load students page', async () => {
    const expectedResponse: StudentsPageResponse = {
      content: [
        {
          id: 1,
          firstName: 'Alex',
          lastName: 'Stone',
          email: 'alex@example.com',
          groupId: 10,
          groupName: 'Java-01',
        },
      ],
      page: 0,
      size: 20,
      totalElements: 1,
      totalPages: 1,
    };

    const responsePromise = firstValueFrom(service.getStudents(0, 20));

    const request = httpTesting.expectOne(
      (candidate) =>
        candidate.url === '/api/v1/students' &&
        candidate.params.get('page') === '0' &&
        candidate.params.get('size') === '20',
    );

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should create student', async () => {
    const requestBody: StudentCreateRequest = {
      firstName: 'Alice',
      lastName: 'Stone',
      email: 'alice@example.com',
      groupId: 10,
    };

    const expectedResponse: StudentResponse = {
      id: 1,
      firstName: 'Alice',
      lastName: 'Stone',
      email: 'alice@example.com',
      groupId: 10,
      groupName: 'Java-01',
    };

    const responsePromise = firstValueFrom(service.createStudent(requestBody));

    const request = httpTesting.expectOne('/api/v1/students');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should get student by id', () => {
    const response: StudentResponse = {
      id: 7,
      firstName: 'Arthur',
      lastName: 'Morgan',
      email: 'arthur.morgan@example.com',
      groupId: 10,
      groupName: 'Exact Sciences Group',
    };

    service.getStudent(7).subscribe((student) => {
      expect(student).toEqual(response);
    });

    const request = httpTesting.expectOne('/api/v1/students/7');

    expect(request.request.method).toBe('GET');

    request.flush(response);
  });

  it('should update student', () => {
    const updateRequest: StudentUpdateRequest = {
      firstName: 'Arthur',
      lastName: 'Morgan',
      email: 'arthur.updated@example.com',
      groupId: 20,
    };

    const response: StudentResponse = {
      id: 7,
      ...updateRequest,
      groupName: 'Updated Group',
    };

    service.updateStudent(7, updateRequest).subscribe((student) => {
      expect(student).toEqual(response);
    });

    const request = httpTesting.expectOne('/api/v1/students/7');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(updateRequest);

    request.flush(response);
  });
  it('should deactivate student', () => {
    service.deactivateStudent(7).subscribe((response) => {
      expect(response).toBeNull();
    });

    const request = httpTesting.expectOne('/api/v1/students/7');

    expect(request.request.method).toBe('DELETE');

    request.flush(null, {
      status: 204,
      statusText: 'No Content',
    });
  });
});
