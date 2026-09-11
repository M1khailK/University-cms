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
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { GroupResponse } from '../../groups/group.models';
import { GroupService } from '../../groups/group.service';
import { StudentCreateRequest } from '../student.models';
import { StudentService } from '../student.service';

function notBlank(control: AbstractControl): ValidationErrors | null {
  const value = control.value;

  return typeof value === 'string' && value.trim().length === 0 ? { blank: true } : null;
}

@Component({
  selector: 'app-student-create',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './student-create.component.html',
  styleUrl: './student-create.component.scss',
})
export class StudentCreateComponent implements OnInit {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly studentService = inject(StudentService);
  private readonly groupService = inject(GroupService);
  private readonly router = inject(Router);

  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly loadingGroups = signal(false);
  protected readonly submitting = signal(false);
  protected readonly groupLoadError = signal<string | null>(null);
  protected readonly submitError = signal<string | null>(null);

  protected readonly form = this.formBuilder.group({
    firstName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    lastName: ['', [Validators.required, notBlank, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(50)]],
    groupId: new FormControl<number | null>(null, {
      validators: [Validators.required],
    }),
  });

  ngOnInit(): void {
    this.loadGroups();
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

    if (this.form.invalid || groupId === null) {
      return;
    }

    const request: StudentCreateRequest = {
      firstName,
      lastName,
      email,
      groupId,
    };

    this.submitting.set(true);

    this.studentService
      .createStudent(request)
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

  protected cancel(): void {
    void this.router.navigateByUrl('/students');
  }

  private loadGroups(): void {
    this.loadingGroups.set(true);
    this.groupLoadError.set(null);

    this.groupService
      .getGroups()
      .pipe(finalize(() => this.loadingGroups.set(false)))
      .subscribe({
        next: (groups) => {
          this.groups.set(groups);
        },
        error: () => {
          this.groupLoadError.set('Failed to load groups.');
        },
      });
  }

  private getSubmitErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to create student.';
    }

    if (error.status === 400) {
      return 'Please check the entered student data.';
    }

    if (error.status === 403) {
      return 'You are not allowed to create students.';
    }

    if (error.status === 409) {
      return 'A student with this email already exists.';
    }

    return 'Failed to create student.';
  }
}
