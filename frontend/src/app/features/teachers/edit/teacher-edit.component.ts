import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import {
  AbstractControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';

import { TeacherUpdateRequest } from '../teacher.models';
import { TeacherService } from '../teacher.service';

@Component({
  selector: 'app-teacher-edit',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './teacher-edit.component.html',
  styleUrl: './teacher-edit.component.scss',
})
export class TeacherEditComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly teacherService = inject(TeacherService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly submitting = signal(false);
  readonly loadError = signal<string | null>(null);
  readonly submitError = signal<string | null>(null);

  readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    lastName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
  });

  private teacherId: number | null = null;

  constructor() {
    this.loadTeacher();
  }

  submit(): void {
    this.submitError.set(null);

    if (this.loading() || this.teacherId === null) {
      return;
    }

    const firstName = this.form.controls.firstName.value.trim();
    const lastName = this.form.controls.lastName.value.trim();
    const email = this.form.controls.email.value.trim();

    this.form.patchValue({
      firstName,
      lastName,
      email,
    });

    this.form.markAllAsTouched();

    if (this.form.invalid) {
      return;
    }

    const request: TeacherUpdateRequest = {
      firstName,
      lastName,
      email,
    };

    this.submitting.set(true);

    this.teacherService
      .updateTeacher(this.teacherId, request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/teachers']);
        },
        error: (error: HttpErrorResponse) => {
          this.submitError.set(this.getSubmitErrorMessage(error));
        },
      });
  }

  cancel(): void {
    void this.router.navigate(['/teachers']);
  }

  private loadTeacher(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    const id = Number(idParam);

    if (idParam === null || !Number.isInteger(id) || id <= 0) {
      this.loading.set(false);
      this.loadError.set('Teacher not found.');
      return;
    }

    this.teacherId = id;

    this.teacherService
      .getTeacher(id)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (teacher) => {
          this.form.setValue({
            firstName: teacher.firstName,
            lastName: teacher.lastName,
            email: teacher.email,
          });
        },
        error: (error: HttpErrorResponse) => {
          this.loadError.set(this.getLoadErrorMessage(error));
        },
      });
  }

  private getLoadErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
      case 403:
        return 'You are not allowed to view teachers.';
      case 404:
        return 'Teacher not found.';
      default:
        return 'Failed to load teacher.';
    }
  }

  private getSubmitErrorMessage(error: HttpErrorResponse): string {
    switch (error.status) {
      case 400:
        return 'Please check the entered teacher data.';
      case 403:
        return 'You are not allowed to update teachers.';
      case 404:
        return 'Teacher not found.';
      case 409:
        return 'A teacher with this email already exists.';
      default:
        return 'Failed to update teacher.';
    }
  }
}

function notBlank(control: AbstractControl<string>): ValidationErrors | null {
  return control.value.trim().length === 0 ? { blank: true } : null;
}
