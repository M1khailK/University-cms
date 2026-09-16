import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { AssistantMessageRequest, AssistantMessageResponse } from './assistant.models';
import { AssistantService } from './assistant.service';

describe('AssistantService', () => {
  let service: AssistantService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AssistantService, provideHttpClientTesting()],
    });

    service = TestBed.inject(AssistantService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should send assistant message', async () => {
    const requestBody: AssistantMessageRequest = {
      message: 'What can you help me with?',
    };

    const expectedResponse: AssistantMessageResponse = {
      answer: 'I can help with University-CMS questions.',
    };

    const responsePromise = firstValueFrom(service.sendMessage(requestBody));

    const request = httpTesting.expectOne('/api/v1/assistant/messages');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestBody);

    request.flush(expectedResponse);

    await expect(responsePromise).resolves.toEqual(expectedResponse);
  });
});
