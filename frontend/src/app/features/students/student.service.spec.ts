import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { StudentService } from './student.service';
import { StudentsPageResponse } from './student.models';

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
});
