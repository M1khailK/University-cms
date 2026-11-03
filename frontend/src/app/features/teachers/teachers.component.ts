import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { filter, finalize, switchMap, tap } from 'rxjs';

import { TeacherDeactivateDialogComponent } from './deactivate/teacher-deactivate-dialog.component';
import { TeacherResponse } from './teacher.models';
import { TeacherService } from './teacher.service';

@Component({
  selector: 'app-teachers',
  imports: [MatButtonModule, MatDialogModule, MatTableModule, RouterLink],
  templateUrl: './teachers.component.html',
  styleUrl: './teachers.component.scss',
})
export class TeachersComponent implements OnInit {
  private readonly teacherService = inject(TeacherService);
  private readonly dialog = inject(MatDialog);

  protected readonly deactivatingTeacherId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);

  protected readonly displayedColumns = ['firstName', 'lastName', 'email', 'actions'];

  protected readonly teachers = signal<TeacherResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTeachers();
  }

  protected deactivateTeacher(teacher: TeacherResponse): void {
    this.actionError.set(null);

    this.dialog
      .open(TeacherDeactivateDialogComponent, {
        data: {
          fullName: `${teacher.firstName} ${teacher.lastName}`,
        },
      })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        tap(() => this.deactivatingTeacherId.set(teacher.id)),
        switchMap(() =>
          this.teacherService
            .deactivateTeacher(teacher.id)
            .pipe(finalize(() => this.deactivatingTeacherId.set(null))),
        ),
      )
      .subscribe({
        next: () => {
          this.loadTeachers();
        },
        error: (error: unknown) => {
          this.actionError.set(this.getDeactivateErrorMessage(error));
        },
      });
  }

  private loadTeachers(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.teacherService
      .getTeachers()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (teachers) => {
          this.teachers.set(teachers);
        },
        error: () => {
          this.errorMessage.set('Failed to load teachers.');
        },
      });
  }

  private getDeactivateErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to deactivate teacher.';
    }

    if (error.status === 403) {
      return 'You are not allowed to deactivate teachers.';
    }

    if (error.status === 404) {
      return 'Teacher was not found or is already inactive.';
    }

    return 'Failed to deactivate teacher.';
  }
}
