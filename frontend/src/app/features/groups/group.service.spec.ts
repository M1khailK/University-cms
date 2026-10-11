import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { GroupResponse, GroupStudentResponse } from './group.models';
import { GroupService } from './group.service';

describe('GroupService', () => {
  let service: GroupService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GroupService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(GroupService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should load groups', async () => {
    const expectedResponse: GroupResponse[] = [
      {
        id: 1,
        name: 'Java-01',
      },
      {
        id: 2,
        name: 'Java-02',
      },
    ];

    const responsePromise = firstValueFrom(service.getGroups());

    const request = httpTesting.expectOne('/api/v1/groups');

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should load a group by id', async () => {
    const expectedResponse: GroupResponse = {
      id: 1,
      name: 'Java-01',
    };

    const responsePromise = firstValueFrom(service.getGroup(1));

    const request = httpTesting.expectOne('/api/v1/groups/1');

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should load students assigned to a group', async () => {
    const expectedResponse: GroupStudentResponse[] = [
      {
        id: 10,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john.doe@example.com',
      },
      {
        id: 11,
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane.smith@example.com',
      },
    ];

    const responsePromise = firstValueFrom(service.getGroupStudents(1));

    const request = httpTesting.expectOne('/api/v1/groups/1/students');

    expect(request.request.method).toBe('GET');

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });

  it('should create a group', async () => {
    const responsePromise = firstValueFrom(
      service.createGroup({
        name: 'Java-03',
      }),
    );

    const request = httpTesting.expectOne('/api/v1/groups');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      name: 'Java-03',
    });

    request.flush({
      id: 3,
      name: 'Java-03',
    });

    await expect(responsePromise).resolves.toEqual({
      id: 3,
      name: 'Java-03',
    });
  });

  it('should update a group', async () => {
    const responsePromise = firstValueFrom(
      service.updateGroup(3, {
        name: 'Java-99',
      }),
    );

    const request = httpTesting.expectOne('/api/v1/groups/3');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({
      name: 'Java-99',
    });

    request.flush({
      id: 3,
      name: 'Java-99',
    });

    await expect(responsePromise).resolves.toEqual({
      id: 3,
      name: 'Java-99',
    });
  });

  it('should delete a group', async () => {
    const responsePromise = firstValueFrom(service.deleteGroup(3));

    const request = httpTesting.expectOne('/api/v1/groups/3');

    expect(request.request.method).toBe('DELETE');

    request.flush(null, {
      status: 204,
      statusText: 'No Content',
    });

    await responsePromise;
  });
});
