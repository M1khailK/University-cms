import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, finalize } from 'rxjs';
import { GroupResponse } from '../../groups/group.models';
import { GroupService } from '../../groups/group.service';
import { StudentUpdateRequest } from '../student.models';
import { StudentService } from '../student.service';

function notBlank(control: AbstractControl): ValidationErrors | null {
  const value = control.value;

  return typeof value === 'string' && value.trim().length === 0 ? { blank: true } : null;
}

@Component({
  selector: 'app-student-edit',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './student-edit.component.html',
  styleUrl: './student-edit.component.scss',
})
export class StudentEditComponent implements OnInit {
  private studentId: number | null = null;
  protected readonly submitting = signal(false);
  protected readonly submitError = signal<string | null>(null);
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly studentService = inject(StudentService);
  private readonly groupService = inject(GroupService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly loadError = signal<string | null>(null);

  protected readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    lastName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    groupId: new FormControl<number | null>(null, {
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    this.loadStudent();
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/students');
  }
  private loadStudent(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.loadError.set('Invalid student id.');
      return;
    }

    this.studentId = id;

    this.loading.set(true);
    this.loadError.set(null);

    forkJoin({
      student: this.studentService.getStudent(id),
      groups: this.groupService.getGroups(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ student, groups }) => {
          this.groups.set(groups);

          this.form.setValue({
            firstName: student.firstName,
            lastName: student.lastName,
            email: student.email,
            groupId: student.groupId,
          });
        },
        error: () => {
          this.loadError.set('Failed to load student.');
        },
      });
  }

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

    const groupId = this.form.controls.groupId.value;

    if (this.form.invalid || groupId === null || this.studentId === null) {
      return;
    }

    const request: StudentUpdateRequest = {
      firstName,
      lastName,
      email,
      groupId,
    };

    this.submitting.set(true);

    this.studentService
      .updateStudent(this.studentId, request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/students');
        },
        error: (error: unknown) => {
          this.submitError.set(this.getSubmitErrorMessage(error));
        },
      });
  }

  private getSubmitErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to update student.';
    }

    if (error.status === 400) {
      return 'Please check the entered student data.';
    }

    if (error.status === 403) {
      return 'You are not allowed to update students.';
    }

    if (error.status === 404) {
      return 'Student not found.';
    }

    if (error.status === 409) {
      return 'A student with this email already exists.';
    }

    return 'Failed to update student.';
  }
}
