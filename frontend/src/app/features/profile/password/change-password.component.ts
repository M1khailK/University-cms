import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal, ViewChild } from '@angular/core';
import {
  AbstractControl,
  FormGroupDirective,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { finalize } from 'rxjs';

import { PasswordChangeRequest } from '../profile.models';
import { ProfileService } from '../profile.service';

@Component({
  selector: 'app-change-password',
  imports: [ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.scss',
})
export class ChangePasswordComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly profileService = inject(ProfileService);

  @ViewChild(FormGroupDirective)
  private formDirective?: FormGroupDirective;

  protected readonly submitting = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.formBuilder.group(
    {
      oldPassword: [
        '',
        [Validators.required, notBlank, Validators.minLength(5), Validators.maxLength(72)],
      ],
      newPassword: [
        '',
        [Validators.required, notBlank, Validators.minLength(5), Validators.maxLength(72)],
      ],
      confirmPassword: [
        '',
        [Validators.required, Validators.minLength(5), Validators.maxLength(72)],
      ],
    },
    {
      validators: passwordsMatch,
    },
  );

  protected submit(): void {
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.form.markAllAsTouched();

    if (this.form.invalid || this.submitting()) {
      return;
    }

    const request: PasswordChangeRequest = {
      oldPassword: this.form.controls.oldPassword.value,
      newPassword: this.form.controls.newPassword.value,
    };

    this.submitting.set(true);

    this.profileService
      .changePassword(request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.resetForm({
            oldPassword: '',
            newPassword: '',
            confirmPassword: '',
          });

          this.successMessage.set('Password changed successfully.');
        },
        error: (error: unknown) => {
          this.resetForm({
            oldPassword: '',
            newPassword: this.form.controls.newPassword.value,
            confirmPassword: this.form.controls.confirmPassword.value,
          });

          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
  }

  private resetForm(values: PasswordFormValue): void {
    if (this.formDirective) {
      this.formDirective.resetForm(values);
      return;
    }

    this.form.reset(values);
  }

  private getErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to change password.';
    }

    const problem = error.error as { title?: string } | null;

    if (error.status === 400 && problem?.title === 'Invalid old password') {
      return 'The current password is incorrect.';
    }

    if (error.status === 400) {
      return 'Please check the entered passwords.';
    }

    if (error.status === 403) {
      return 'You are not allowed to change this password.';
    }

    return 'Failed to change password.';
  }
}

interface PasswordFormValue {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

function notBlank(control: AbstractControl<string>): ValidationErrors | null {
  return control.value.trim().length === 0 ? { blank: true } : null;
}

function passwordsMatch(control: AbstractControl): ValidationErrors | null {
  const newPassword = control.get('newPassword')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;

  return newPassword === confirmPassword ? null : { passwordMismatch: true };
}
