import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { GroupResponse } from './group.models';
import { GroupService } from './group.service';

describe('GroupService', () => {
  let service: GroupService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GroupService, provideHttpClientTesting()],
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
});