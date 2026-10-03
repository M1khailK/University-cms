import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  LessonMaterialStatusResponse,
  LessonMaterialUploadIntentResponse,
} from './lesson-material.models';
import { LessonMaterialService } from './lesson-material.service';

describe('LessonMaterialService', () => {
  let service: LessonMaterialService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LessonMaterialService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(LessonMaterialService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should create an upload intent from the selected file', () => {
    const file = new File(['pdf-content'], 'lecture.pdf', { type: 'application/pdf' });

    const response: LessonMaterialUploadIntentResponse = {
      materialId: 42,
      uploadUrl: 'https://storage.example.com/upload',
      uploadMethod: 'POST',
      expiresAt: '2026-09-28T12:10:00Z',
      contentType: 'application/pdf',
      formFields: {
        key: 'lesson-materials/17/material-id',
        policy: 'encoded-policy',
      },
    };

    service.createUploadIntent(17, file).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const request = httpTestingController.expectOne('/api/v1/lessons/17/materials/upload-intent');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      originalFilename: 'lecture.pdf',
      contentType: 'application/pdf',
      expectedSizeBytes: file.size,
    });

    request.flush(response);
  });

  it('should upload the file using presigned form fields', () => {
    const file = new File(['pdf-content'], 'lecture.pdf', { type: 'application/pdf' });

    const uploadIntent: LessonMaterialUploadIntentResponse = {
      materialId: 42,
      uploadUrl: 'https://storage.example.com/upload',
      uploadMethod: 'POST',
      expiresAt: '2026-09-28T12:10:00Z',
      contentType: 'application/pdf',
      formFields: {
        key: 'lesson-materials/17/material-id',
        policy: 'encoded-policy',
      },
    };

    service.uploadFile(uploadIntent, file).subscribe();

    const request = httpTestingController.expectOne(uploadIntent.uploadUrl);

    expect(request.request.method).toBe('POST');
    expect(request.request.reportProgress).toBe(true);
    expect(request.request.responseType).toBe('text');
    expect(request.request.body).toBeInstanceOf(FormData);

    const formData = request.request.body as FormData;

    expect(formData.get('key')).toBe('lesson-materials/17/material-id');
    expect(formData.get('policy')).toBe('encoded-policy');
    const uploadedFile = formData.get('file');

    expect(uploadedFile).toBeInstanceOf(File);
    expect((uploadedFile as File).name).toBe(file.name);
    expect((uploadedFile as File).type).toBe(file.type);
    expect((uploadedFile as File).size).toBe(file.size);

    request.flush('', {
      status: 204,
      statusText: 'No Content',
    });
  });

  it('should load material processing status', () => {
    const response: LessonMaterialStatusResponse = {
      materialId: 42,
      lessonId: 17,
      originalFilename: 'lecture.pdf',
      contentType: 'application/pdf',
      status: 'READY',
      expectedSizeBytes: 1024,
      actualSizeBytes: 1024,
      createdAt: '2026-09-28T12:00:00Z',
      uploadedAt: '2026-09-28T12:01:00Z',
      processingStartedAt: '2026-09-28T12:02:00Z',
      processedAt: '2026-09-28T12:03:00Z',
      failureReason: null,
    };

    service.getMaterialStatus(17, 42).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const request = httpTestingController.expectOne('/api/v1/lessons/17/materials/42');

    expect(request.request.method).toBe('GET');

    request.flush(response);
  });
});
