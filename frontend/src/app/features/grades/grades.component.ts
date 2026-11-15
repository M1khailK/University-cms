import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';

import { AuthService } from '../auth/auth.service';
import { GradeResponse } from './grade.models';
import { GradeService } from './grade.service';

@Component({
  selector: 'app-grades',
  imports: [MatTableModule],
  templateUrl: './grades.component.html',
  styleUrl: './grades.component.scss',
})
export class GradesComponent implements OnInit {
  private readonly gradeService = inject(GradeService);
  private readonly authService = inject(AuthService);

  protected readonly isStudent = this.authService.isStudent;

  protected readonly grades = signal<GradeResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly displayedColumns = computed(() =>
    this.isStudent()
      ? ['lesson', 'date', 'time', 'value']
      : ['student', 'lesson', 'date', 'time', 'value'],
  );

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

  private getErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 403) {
      return 'You are not allowed to view grades.';
    }

    return 'Failed to load grades.';
  }
}
