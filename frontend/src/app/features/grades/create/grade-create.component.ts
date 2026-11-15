import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';

import {
  GradeCreateRequest,
  GradeLessonOptionResponse,
  GradeStudentOptionResponse,
} from '../grade.models';
import { GradeService } from '../grade.service';

@Component({
  selector: 'app-grade-create',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatSelectModule],
  templateUrl: './grade-create.component.html',
  styleUrl: './grade-create.component.scss',
})
export class GradeCreateComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly gradeService = inject(GradeService);
  private readonly router = inject(Router);

  protected readonly lessons = signal<GradeLessonOptionResponse[]>([]);
  protected readonly optionsLoading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly gradeValues = [1, 2, 3, 4, 5];

  protected readonly form = this.formBuilder.group({
    lessonId: [null as number | null, [Validators.required, Validators.min(1)]],
    studentId: [
      {
        value: null as number | null,
        disabled: true,
      },
      [Validators.required, Validators.min(1)],
    ],
    value: [null as number | null, [Validators.required, Validators.min(1), Validators.max(5)]],
  });

  constructor() {
    this.form.controls.lessonId.valueChanges.subscribe((lessonId) => {
      const studentControl = this.form.controls.studentId;

      studentControl.reset();

      if (lessonId === null) {
        studentControl.disable();
      } else {
        studentControl.enable();
      }
    });
  }

  ngOnInit(): void {
    this.loadOptions();
  }

  protected studentsForSelectedLesson(): GradeStudentOptionResponse[] {
    const lessonId = this.form.controls.lessonId.value;

    if (lessonId === null) {
      return [];
    }

    return this.lessons().find((lesson) => lesson.id === lessonId)?.students ?? [];
  }

  protected submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();

    const request: GradeCreateRequest = {
      lessonId: value.lessonId!,
      studentId: value.studentId!,
      value: value.value!,
    };

    this.submitting.set(true);

    this.gradeService
      .createGrade(request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/grades');
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getCreateErrorMessage(error));
        },
      });
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/grades');
  }

  private loadOptions(): void {
    this.optionsLoading.set(true);
    this.errorMessage.set(null);

    this.gradeService
      .getGradeCreationOptions()
      .pipe(finalize(() => this.optionsLoading.set(false)))
      .subscribe({
        next: (response) => {
          this.lessons.set(response.lessons);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getOptionsErrorMessage(error));
        },
      });
  }

  private getOptionsErrorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 403) {
      return 'You are not allowed to create grades.';
    }

    return 'Failed to load grade options.';
  }

  private getCreateErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to create grade.';
    }

    if (error.status === 400) {
      return 'Grade data is invalid.';
    }

    if (error.status === 403) {
      return 'You are not allowed to create grades.';
    }

    if (error.status === 404) {
      return 'Selected lesson or student was not found.';
    }

    if (error.status === 409) {
      return 'Selected student does not belong to the lesson group.';
    }

    return 'Failed to create grade.';
  }
}
