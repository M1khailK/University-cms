import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

export interface GradeDeleteDialogData {
  studentName: string;
  lessonName: string;
  value: number;
}

@Component({
  selector: 'app-grade-delete-dialog',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Delete grade</h2>

    <mat-dialog-content>
      Delete grade {{ data.value }} for {{ data.studentName }} from {{ data.lessonName }}? This
      action cannot be undone.
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>

      <button mat-flat-button [mat-dialog-close]="true">Delete</button>
    </mat-dialog-actions>
  `,
})
export class GradeDeleteDialogComponent {
  protected readonly data = inject<GradeDeleteDialogData>(MAT_DIALOG_DATA);
}
