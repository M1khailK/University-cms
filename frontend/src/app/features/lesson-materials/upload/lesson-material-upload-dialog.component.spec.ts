import { HttpResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { ScheduleLessonResponse } from '../../schedule/schedule.models';
import {
  LessonMaterialStatusResponse,
  LessonMaterialUploadIntentResponse,
} from '../lesson-material.models';
import { LessonMaterialService } from '../lesson-material.service';
import { LessonMaterialUploadDialogComponent } from './lesson-material-upload-dialog.component';

describe('LessonMaterialUploadDialogComponent', () => {
  let component: LessonMaterialUploadDialogComponent;

  const lesson: ScheduleLessonResponse = {
    id: 17,
    name: 'Vector Search',
    date: '2026-10-03',
    startTime: '10:00:00',
    endTime: '11:30:00',
    subjectId: 3,
    subjectName: 'Software Architecture',
    groupId: 5,
    groupName: 'SA-01',
    teacherId: 7,
    teacherFirstName: 'Michael',
    teacherLastName: 'Fox',
  };

  const lessonMaterialServiceMock = {
    createUploadIntent: vi.fn(),
    uploadFile: vi.fn(),
    getMaterialStatus: vi.fn(),
  };

  beforeEach(async () => {
    lessonMaterialServiceMock.createUploadIntent.mockReset();
    lessonMaterialServiceMock.uploadFile.mockReset();
    lessonMaterialServiceMock.getMaterialStatus.mockReset();

    await TestBed.configureTestingModule({
      imports: [LessonMaterialUploadDialogComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: lesson,
        },
        {
          provide: MatDialogRef,
          useValue: {
            close: vi.fn(),
          },
        },
        {
          provide: LessonMaterialService,
          useValue: lessonMaterialServiceMock,
        },
      ],
    }).compileComponents();

    component = TestBed.createComponent(LessonMaterialUploadDialogComponent).componentInstance;
  });

  it('should reject a non-PDF file', () => {
    const file = new File(['plain text'], 'notes.txt', { type: 'text/plain' });

    component['selectFile'](fileSelectionEvent(file));

    expect(component['selectedFile']()).toBeNull();
    expect(component['stage']()).toBe('error');
    expect(component['errorMessage']()).toBe('Choose a PDF file.');
  });

  it('should upload and reach ready state', async () => {
    const file = new File(['pdf-content'], 'lecture.pdf', { type: 'application/pdf' });

    const uploadIntent: LessonMaterialUploadIntentResponse = {
      materialId: 42,
      uploadUrl: 'https://storage.example.com/upload',
      uploadMethod: 'POST',
      expiresAt: '2026-10-03T15:10:00Z',
      contentType: 'application/pdf',
      formFields: {
        key: 'lesson-materials/17/material-id',
        policy: 'encoded-policy',
      },
    };

    const readyStatus: LessonMaterialStatusResponse = {
      materialId: 42,
      lessonId: 17,
      originalFilename: 'lecture.pdf',
      contentType: 'application/pdf',
      status: 'READY',
      expectedSizeBytes: file.size,
      actualSizeBytes: file.size,
      createdAt: '2026-10-03T15:00:00Z',
      uploadedAt: '2026-10-03T15:01:00Z',
      processingStartedAt: '2026-10-03T15:02:00Z',
      processedAt: '2026-10-03T15:03:00Z',
      failureReason: null,
    };

    lessonMaterialServiceMock.createUploadIntent.mockReturnValue(of(uploadIntent));

    lessonMaterialServiceMock.uploadFile.mockReturnValue(
      of(
        new HttpResponse<string>({
          body: '',
          status: 204,
        }),
      ),
    );

    lessonMaterialServiceMock.getMaterialStatus.mockReturnValue(of(readyStatus));

    component['selectFile'](fileSelectionEvent(file));
    component['startUpload']();

    await vi.waitFor(() => {
      expect(component['stage']()).toBe('ready');
    });

    expect(lessonMaterialServiceMock.createUploadIntent).toHaveBeenCalledWith(17, file);

    expect(lessonMaterialServiceMock.uploadFile).toHaveBeenCalledWith(uploadIntent, file);

    expect(lessonMaterialServiceMock.getMaterialStatus).toHaveBeenCalledWith(17, 42);

    expect(component['stage']()).toBe('ready');
    expect(component['uploadProgress']()).toBe(100);
    expect(component['materialStatus']()).toEqual(readyStatus);
  });
});

function fileSelectionEvent(file: File): Event {
  const input = {
    files: {
      item: () => file,
    },
    value: file.name,
  } as unknown as HTMLInputElement;

  return {
    target: input,
  } as unknown as Event;
}
