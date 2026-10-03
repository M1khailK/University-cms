import { HttpClient, HttpEvent } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  LessonMaterialStatusResponse,
  LessonMaterialUploadIntentResponse,
} from './lesson-material.models';

@Injectable({
  providedIn: 'root',
})
export class LessonMaterialService {
  private readonly http = inject(HttpClient);

  createUploadIntent(lessonId: number, file: File): Observable<LessonMaterialUploadIntentResponse> {
    return this.http.post<LessonMaterialUploadIntentResponse>(
      `/api/v1/lessons/${lessonId}/materials/upload-intent`,
      {
        originalFilename: file.name,
        contentType: file.type,
        expectedSizeBytes: file.size,
      },
    );
  }

  uploadFile(
    uploadIntent: LessonMaterialUploadIntentResponse,
    file: File,
  ): Observable<HttpEvent<string>> {
    const formData = new FormData();

    for (const [name, value] of Object.entries(uploadIntent.formFields)) {
      formData.append(name, value);
    }

    formData.append('file', file, file.name);

    return this.http.request(uploadIntent.uploadMethod, uploadIntent.uploadUrl, {
      body: formData,
      observe: 'events',
      reportProgress: true,
      responseType: 'text',
    });
  }

  getMaterialStatus(
    lessonId: number,
    materialId: number,
  ): Observable<LessonMaterialStatusResponse> {
    return this.http.get<LessonMaterialStatusResponse>(
      `/api/v1/lessons/${lessonId}/materials/${materialId}`,
    );
  }
}
