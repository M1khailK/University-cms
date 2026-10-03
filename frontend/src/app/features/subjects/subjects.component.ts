import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { filter, finalize, switchMap, tap } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { SubjectFormDialogComponent } from './form/subject-form-dialog.component';
import { SubjectRequest, SubjectResponse } from './subject.models';
import { SubjectService } from './subject.service';

@Component({
  selector: 'app-subjects',
  imports: [RouterLink, MatButtonModule, MatDialogModule],
  templateUrl: './subjects.component.html',
  styleUrl: './subjects.component.scss',
})
export class SubjectsComponent implements OnInit {
  private readonly subjectService = inject(SubjectService);
  private readonly authService = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected readonly isAdmin = this.authService.isAdmin;

  protected readonly subjects = signal<SubjectResponse[]>([]);
  protected readonly query = signal('');
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);

  protected readonly deletingSubjectId = signal<number | null>(null);

  protected readonly confirmingDeleteId = signal<number | null>(null);

  protected readonly errorMessage = signal<string | null>(null);
  protected readonly actionError = signal<string | null>(null);

  protected readonly filteredSubjects = computed(() => {
    const normalizedQuery = this.query().trim().toLocaleLowerCase();

    if (!normalizedQuery) {
      return this.subjects();
    }

    return this.subjects().filter((subject) =>
      subject.name.toLocaleLowerCase().includes(normalizedQuery),
    );
  });

  ngOnInit(): void {
    this.loadSubjects();
  }

  protected updateQuery(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.query.set(input.value);
  }

  protected openCreateDialog(): void {
    this.openFormDialog(null);
  }

  protected openEditDialog(subject: SubjectResponse): void {
    this.openFormDialog(subject);
  }

  protected requestDelete(subjectId: number): void {
    this.actionError.set(null);
    this.confirmingDeleteId.set(subjectId);
  }

  protected cancelDelete(): void {
    this.confirmingDeleteId.set(null);
  }

  protected deleteSubject(subject: SubjectResponse): void {
    if (this.deletingSubjectId() !== null) {
      return;
    }

    this.actionError.set(null);
    this.deletingSubjectId.set(subject.id);

    this.subjectService
      .deleteSubject(subject.id)
      .pipe(
        finalize(() => {
          this.deletingSubjectId.set(null);
          this.confirmingDeleteId.set(null);
        }),
      )
      .subscribe({
        next: () => {
          this.subjects.update((subjects) =>
            subjects.filter((candidate) => candidate.id !== subject.id),
          );
        },
        error: (error: unknown) => {
          this.actionError.set(this.getActionErrorMessage(error, 'delete'));
        },
      });
  }

  private loadSubjects(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.subjectService
      .getSubjects()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (subjects) => {
          this.subjects.set(this.sortSubjects(subjects));
        },
        error: () => {
          this.errorMessage.set('The subject catalog could not be loaded.');
        },
      });
  }

  private openFormDialog(subject: SubjectResponse | null): void {
    this.actionError.set(null);

    this.dialog
      .open(SubjectFormDialogComponent, {
        data: { subject },
        width: '30rem',
        maxWidth: 'calc(100vw - 2rem)',
        autoFocus: 'first-tabbable',
      })
      .afterClosed()
      .pipe(
        filter((request): request is SubjectRequest => request !== undefined),
        tap(() => this.saving.set(true)),
        switchMap((request) => {
          const operation =
            subject === null
              ? this.subjectService.createSubject(request)
              : this.subjectService.updateSubject(subject.id, request);

          return operation.pipe(finalize(() => this.saving.set(false)));
        }),
      )
      .subscribe({
        next: (savedSubject) => {
          this.subjects.update((subjects) => {
            const remaining = subjects.filter((candidate) => candidate.id !== savedSubject.id);

            return this.sortSubjects([...remaining, savedSubject]);
          });
        },
        error: (error: unknown) => {
          this.actionError.set(
            this.getActionErrorMessage(error, subject === null ? 'create' : 'update'),
          );
        },
      });
  }

  private sortSubjects(subjects: SubjectResponse[]): SubjectResponse[] {
    return [...subjects].sort((left, right) => left.name.localeCompare(right.name));
  }

  private getActionErrorMessage(error: unknown, action: 'create' | 'update' | 'delete'): string {
    if (!(error instanceof HttpErrorResponse)) {
      return `Failed to ${action} subject.`;
    }

    if (error.status === 403) {
      return 'Administrator permissions are required.';
    }

    if (error.status === 404) {
      return 'The subject no longer exists.';
    }

    if (error.status === 409) {
      return 'The subject cannot be deleted while it is ' + 'used by university lessons.';
    }

    if (error.status === 400) {
      return 'Check the subject name and try again.';
    }

    return `Failed to ${action} subject.`;
  }
}
