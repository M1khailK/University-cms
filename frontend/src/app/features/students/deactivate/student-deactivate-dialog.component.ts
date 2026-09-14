import { Component, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export interface StudentDeactivateDialogData {
  fullName: string;
}

@Component({
  selector: 'app-student-deactivate-dialog',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Deactivate student</h2>

    <mat-dialog-content>
      Deactivate {{ data.fullName }}?
      The student will no longer be able to access the system
      and will disappear from the active students list.
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">
        Cancel
      </button>

      <button mat-flat-button [mat-dialog-close]="true">
        Deactivate
      </button>
    </mat-dialog-actions>
  `,
})
export class StudentDeactivateDialogComponent {
  protected readonly data =
    inject<StudentDeactivateDialogData>(MAT_DIALOG_DATA);
}