import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { filter, finalize, switchMap, tap } from 'rxjs';

import { LessonDeleteDialogComponent } from './delete/lesson-delete-dialog.component';
import { LessonResponse } from './lesson.models';
import { LessonService } from './lesson.service';

@Component({
  selector: 'app-lessons',
  imports: [MatButtonModule, MatTableModule, MatDialogModule, RouterLink],
  templateUrl: './lessons.component.html',
  styleUrl: './lessons.component.scss',
})
export class LessonsComponent implements OnInit {
  private readonly lessonService = inject(LessonService);

  protected readonly displayedColumns = [
    'name',
    'date',
    'time',
    'subject',
    'group',
    'teacher',
    'actions',
  ];

  private readonly dialog = inject(MatDialog);
  protected readonly lessons = signal<LessonResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly deletingLessonId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);

  ngOnInit(): void {
    this.loadLessons();
  }

  private loadLessons(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.lessonService
      .getLessons()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (lessons) => {
          this.lessons.set(lessons);
        },
        error: () => {
          this.errorMessage.set('Failed to load lessons.');
        },
      });
  }
  protected deleteLesson(lesson: LessonResponse): void {
    this.actionError.set(null);

    this.dialog
      .open(LessonDeleteDialogComponent, {
        data: {
          name: lesson.name,
          date: lesson.date,
          startTime: lesson.startTime,
        },
      })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        tap(() => this.deletingLessonId.set(lesson.id)),
        switchMap(() =>
          this.lessonService
            .deleteLesson(lesson.id)
            .pipe(finalize(() => this.deletingLessonId.set(null))),
        ),
      )
      .subscribe({
        next: () => {
          this.loadLessons();
        },
        error: (error: unknown) => {
          this.actionError.set(this.getDeleteErrorMessage(error));
        },
      });
  }

  private getDeleteErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to delete lesson.';
    }

    if (error.status === 403) {
      return 'You are not allowed to delete lessons.';
    }

    if (error.status === 404) {
      return 'Lesson was not found.';
    }

    return 'Failed to delete lesson.';
  }
}
