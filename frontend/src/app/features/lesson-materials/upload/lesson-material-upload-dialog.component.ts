import { HttpErrorResponse, HttpEvent, HttpEventType } from '@angular/common/http';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { filter, Observable, switchMap, take, tap, timeout, TimeoutError, timer } from 'rxjs';
import { ScheduleLessonResponse } from '../../schedule/schedule.models';
import { LessonMaterialStatusResponse } from '../lesson-material.models';
import { LessonMaterialService } from '../lesson-material.service';

type UploadStage =
  'idle' | 'requesting' | 'uploading' | 'processing' | 'ready' | 'failed' | 'error';

@Component({
  selector: 'app-lesson-material-upload-dialog',
  imports: [MatButtonModule, MatDialogModule, MatProgressBarModule],
  templateUrl: './lesson-material-upload-dialog.component.html',
  styleUrl: './lesson-material-upload-dialog.component.scss',
})
export class LessonMaterialUploadDialogComponent {
  private static readonly MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024;

  private static readonly POLLING_INTERVAL_MS = 2000;
  private static readonly POLLING_TIMEOUT_MS = 120_000;

  private readonly lessonMaterialService = inject(LessonMaterialService);

  private readonly destroyRef = inject(DestroyRef);

  protected readonly lesson = inject<ScheduleLessonResponse>(MAT_DIALOG_DATA);

  protected readonly selectedFile = signal<File | null>(null);
  protected readonly stage = signal<UploadStage>('idle');
  protected readonly uploadProgress = signal(0);

  protected readonly materialStatus = signal<LessonMaterialStatusResponse | null>(null);

  protected readonly errorMessage = signal<string | null>(null);

  protected readonly isBusy = computed(() => {
    const stage = this.stage();

    return stage === 'requesting' || stage === 'uploading' || stage === 'processing';
  });

  protected readonly canUpload = computed(() => {
    const stage = this.stage();

    return (
      this.selectedFile() !== null && (stage === 'idle' || stage === 'error' || stage === 'failed')
    );
  });

  protected selectFile(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.item(0) ?? null;

    input.value = '';

    if (file === null) {
      return;
    }

    const validationError = this.validateFile(file);

    if (validationError !== null) {
      this.selectedFile.set(null);
      this.errorMessage.set(validationError);
      this.stage.set('error');
      return;
    }

    this.selectedFile.set(file);
    this.materialStatus.set(null);
    this.uploadProgress.set(0);
    this.errorMessage.set(null);
    this.stage.set('idle');
  }

  protected startUpload(): void {
    const file = this.selectedFile();

    if (file === null || !this.canUpload()) {
      return;
    }

    this.uploadProgress.set(0);
    this.materialStatus.set(null);
    this.errorMessage.set(null);
    this.stage.set('requesting');

    this.lessonMaterialService
      .createUploadIntent(this.lesson.id, file)
      .pipe(
        switchMap((uploadIntent) => {
          this.stage.set('uploading');

          return this.lessonMaterialService.uploadFile(uploadIntent, file).pipe(
            tap((event) => this.updateUploadProgress(event)),
            filter((event) => event.type === HttpEventType.Response),
            take(1),
            switchMap(() => {
              this.uploadProgress.set(100);
              this.stage.set('processing');

              return this.pollUntilTerminal(this.lesson.id, uploadIntent.materialId);
            }),
          );
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (status) => {
          if (status.status === 'READY') {
            this.stage.set('ready');
            return;
          }

          this.stage.set('failed');
          this.errorMessage.set(status.failureReason ?? 'The PDF could not be processed.');
        },
        error: (error: unknown) => {
          this.stage.set('error');
          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
  }

  protected clearSelection(): void {
    if (this.isBusy()) {
      return;
    }

    this.selectedFile.set(null);
    this.materialStatus.set(null);
    this.uploadProgress.set(0);
    this.errorMessage.set(null);
    this.stage.set('idle');
  }

  protected formatBytes(bytes: number): string {
    if (bytes < 1024) {
      return `${bytes} B`;
    }

    if (bytes < 1024 * 1024) {
      return `${(bytes / 1024).toFixed(1)} KB`;
    }

    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  private validateFile(file: File): string | null {
    if (file.type !== 'application/pdf') {
      return 'Choose a PDF file.';
    }

    if (file.size <= 0) {
      return 'The selected PDF is empty.';
    }

    if (file.size > LessonMaterialUploadDialogComponent.MAX_FILE_SIZE_BYTES) {
      return 'The PDF must not exceed 10 MB.';
    }

    if (file.name.length > 255) {
      return 'The filename must not exceed 255 characters.';
    }

    return null;
  }

  private updateUploadProgress(event: HttpEvent<string>): void {
    if (event.type !== HttpEventType.UploadProgress || event.total === undefined) {
      return;
    }

    this.uploadProgress.set(Math.round((event.loaded / event.total) * 100));
  }

  private pollUntilTerminal(
    lessonId: number,
    materialId: number,
  ): Observable<LessonMaterialStatusResponse> {
    return timer(0, LessonMaterialUploadDialogComponent.POLLING_INTERVAL_MS).pipe(
      switchMap(() => this.lessonMaterialService.getMaterialStatus(lessonId, materialId)),
      tap((status) => this.materialStatus.set(status)),
      filter((status) => status.status === 'READY' || status.status === 'FAILED'),
      take(1),
      timeout({
        first: LessonMaterialUploadDialogComponent.POLLING_TIMEOUT_MS,
      }),
    );
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof TimeoutError) {
      return (
        'Processing is taking longer than expected. ' + 'Close this window and check again later.'
      );
    }

    if (!(error instanceof HttpErrorResponse)) {
      return 'The material upload failed.';
    }

    if (error.status === 0) {
      return (
        'The storage service could not be reached. ' +
        'Check the connection and CORS configuration.'
      );
    }

    if (error.status === 400) {
      return 'The selected file was rejected.';
    }

    if (error.status === 403) {
      return 'You are not allowed to upload materials ' + 'for this lesson.';
    }

    if (error.status === 404) {
      return 'The lesson material could not be found.';
    }

    if (error.status === 503) {
      return 'File storage is temporarily unavailable.';
    }

    return 'The material upload failed.';
  }
}
