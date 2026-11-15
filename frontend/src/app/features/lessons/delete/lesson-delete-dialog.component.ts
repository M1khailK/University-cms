import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

export interface LessonDeleteDialogData {
  name: string;
  date: string;
  startTime: string;
}

@Component({
  selector: 'app-lesson-delete-dialog',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Delete lesson</h2>

    <mat-dialog-content>
      Delete {{ data.name }} on {{ data.date }} at {{ data.startTime.slice(0, 5) }}? This action
      cannot be undone.
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>

      <button mat-flat-button [mat-dialog-close]="true">Delete</button>
    </mat-dialog-actions>
  `,
})
export class LessonDeleteDialogComponent {
  protected readonly data = inject<LessonDeleteDialogData>(MAT_DIALOG_DATA);
}
