import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { GroupRequest, GroupResponse } from '../group.models';

export interface GroupFormDialogData {
  group: GroupResponse | null;
}

@Component({
  selector: 'app-group-form-dialog',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './group-form-dialog.component.html',
  styleUrl: './group-form-dialog.component.scss',
})
export class GroupFormDialogComponent {
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly dialogRef = inject(MatDialogRef<GroupFormDialogComponent>);

  protected readonly data = inject<GroupFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.formBuilder.group({
    name: [this.data.group?.name ?? '', [Validators.required, Validators.maxLength(50)]],
  });

  protected readonly editing = this.data.group !== null;

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

    const request: GroupRequest = { name };

    this.dialogRef.close(request);
  }
}
