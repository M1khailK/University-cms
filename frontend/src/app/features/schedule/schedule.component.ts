import { Component, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { ScheduleLessonResponse } from './schedule.models';
import { ScheduleService } from './schedule.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { LessonMaterialUploadDialogComponent } from '../lesson-materials/upload/lesson-material-upload-dialog.component';
@Component({
  selector: 'app-schedule',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatDialogModule,
  ],
  templateUrl: './schedule.component.html',
  styleUrl: './schedule.component.scss',
})
export class ScheduleComponent {
  private readonly scheduleService = inject(ScheduleService);
  private readonly authService = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  protected readonly isTeacher = this.authService.isTeacher;
  protected readonly lessons = signal<ScheduleLessonResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly loaded = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly displayedColumns = computed(() => {
    const columns = ['date', 'time', 'lesson', 'subject', 'group', 'teacher'];

    return this.isTeacher() ? [...columns, 'materials'] : columns;
  });

  protected readonly form = new FormGroup({
    from: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
    to: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required],
    }),
  });

  protected loadSchedule(): void {
    if (this.form.invalid || this.loading()) {
      return;
    }

    const { from, to } = this.form.getRawValue();

    if (to < from) {
      this.errorMessage.set('End date cannot be before start date.');
      this.lessons.set([]);
      return;
    }

    this.loaded.set(false);
    this.loading.set(true);
    this.errorMessage.set(null);

    this.scheduleService
      .getMySchedule(from, to)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (lessons) => {
          this.lessons.set(lessons);
          this.loaded.set(true);
        },
        error: (error: unknown) => {
          this.lessons.set([]);
          this.loaded.set(true);
          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
  }

  protected openMaterialUpload(lesson: ScheduleLessonResponse): void {
    this.dialog.open(LessonMaterialUploadDialogComponent, {
      data: lesson,
      width: '34rem',
      maxWidth: 'calc(100vw - 2rem)',
      disableClose: true,
      autoFocus: false,
    });
  }

  private getErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to load schedule.';
    }

    if (error.status === 403) {
      return 'You are not allowed to view a personal schedule.';
    }

    if (error.status === 400) {
      return 'The selected date range is invalid.';
    }

    return 'Failed to load schedule.';
  }
}
