import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { filter, finalize, switchMap, tap } from 'rxjs';

import { GradeDeleteDialogComponent } from './delete/grade-delete-dialog.component';
import { AuthService } from '../auth/auth.service';
import { GradeResponse } from './grade.models';
import { GradeService } from './grade.service';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-grades',
  imports: [MatButtonModule, MatTableModule, MatDialogModule, RouterLink],
  templateUrl: './grades.component.html',
  styleUrl: './grades.component.scss',
})
export class GradesComponent implements OnInit {
  private readonly gradeService = inject(GradeService);
  private readonly authService = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  protected readonly isStudent = this.authService.isStudent;
  protected readonly isTeacher = this.authService.isTeacher;

  protected readonly grades = signal<GradeResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly deletingGradeId = signal<number | null>(null);

  protected readonly actionError = signal<string | null>(null);

  protected readonly displayedColumns = computed(() => {
    const columns = this.isStudent()
      ? ['lesson', 'date', 'time', 'value']
      : ['student', 'lesson', 'date', 'time', 'value'];

    return this.isTeacher() ? [...columns, 'actions'] : columns;
  });

  ngOnInit(): void {
    this.loadGrades();
  }

  private loadGrades(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const request$ = this.isStudent()
      ? this.gradeService.getMyGrades()
      : this.gradeService.getGrades();

    request$.pipe(finalize(() => this.loading.set(false))).subscribe({
      next: (grades) => {
        this.grades.set(grades);
      },
      error: (error: unknown) => {
        this.grades.set([]);
        this.errorMessage.set(this.getErrorMessage(error));
      },
    });
  }

  protected deleteGrade(grade: GradeResponse): void {
    this.actionError.set(null);

    this.dialog
      .open(GradeDeleteDialogComponent, {
        data: {
          studentName: `${grade.studentFirstName} ${grade.studentLastName}`,
          lessonName: grade.lessonName,
          value: grade.value,
        },
      })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        tap(() => this.deletingGradeId.set(grade.id)),
        switchMap(() =>
          this.gradeService
            .deleteGrade(grade.id)
            .pipe(finalize(() => this.deletingGradeId.set(null))),
        ),
      )
      .subscribe({
        next: () => {
          this.loadGrades();
        },
        error: (error: unknown) => {
          this.actionError.set(this.getDeleteErrorMessage(error));
        },
      });
  }

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 403) {
      return 'You are not allowed to view grades.';
    }

    return 'Failed to load grades.';
  }
  private getDeleteErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to delete grade.';
    }

    if (error.status === 403) {
      return 'You are not allowed to delete this grade.';
    }

    if (error.status === 404) {
      return 'Grade was not found.';
    }

    return 'Failed to delete grade.';
  }
}
