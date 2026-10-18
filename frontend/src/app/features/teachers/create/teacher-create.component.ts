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
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { TeacherCreateRequest } from '../teacher.models';
import { TeacherService } from '../teacher.service';

function notBlank(control: AbstractControl): ValidationErrors | null {
  const value = control.value;

  return typeof value === 'string' && value.trim().length === 0 ? { blank: true } : null;
}

@Component({
  selector: 'app-teacher-create',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './teacher-create.component.html',
  styleUrl: './teacher-create.component.scss',
})
export class TeacherCreateComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly teacherService = inject(TeacherService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);
  protected readonly submitError = signal<string | null>(null);

  protected readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    lastName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
  });

  protected submit(): void {
    this.submitError.set(null);

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

    const request: TeacherCreateRequest = {
      firstName,
      lastName,
      email,
    };

    this.submitting.set(true);

    this.teacherService
      .createTeacher(request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/teachers');
        },
        error: (error: unknown) => {
          this.submitError.set(this.getSubmitErrorMessage(error));
        },
      });
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/teachers');
  }

  private getSubmitErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to create teacher.';
    }

    if (error.status === 400) {
      return 'Please check the entered teacher data.';
    }

    if (error.status === 403) {
      return 'You are not allowed to create teachers.';
    }

    if (error.status === 409) {
      return 'A teacher with this email already exists.';
    }

    return 'Failed to create teacher.';
  }
}
