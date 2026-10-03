import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SubjectRequest, SubjectResponse } from '../subject.models';

export interface SubjectFormDialogData {
  subject: SubjectResponse | null;
}

@Component({
  selector: 'app-subject-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './subject-form-dialog.component.html',
  styleUrl: './subject-form-dialog.component.scss',
})
export class SubjectFormDialogComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);

  private readonly dialogRef = inject(MatDialogRef<SubjectFormDialogComponent>);

  protected readonly data = inject<SubjectFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.formBuilder.group({
    name: [this.data.subject?.name ?? '', [Validators.required, Validators.maxLength(50)]],
  });

  protected readonly editing = this.data.subject !== null;

  protected submit(): void {
    const name = this.form.controls.name.value.trim();

    if (name.length === 0) {
      this.form.controls.name.setErrors({
        required: true,
      });
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: SubjectRequest = { name };

    this.dialogRef.close(request);
  }
}
