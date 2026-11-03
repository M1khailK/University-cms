import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

export interface TeacherDeactivateDialogData {
  fullName: string;
}

@Component({
  selector: 'app-teacher-deactivate-dialog',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>Deactivate teacher</h2>

    <mat-dialog-content>
      Deactivate {{ data.fullName }}? The teacher will no longer be able to access the system and
      will disappear from the active teachers list.
    </mat-dialog-content>

    <mat-dialog-actions align="end">
      <button mat-button [mat-dialog-close]="false">Cancel</button>

      <button mat-flat-button [mat-dialog-close]="true">Deactivate</button>
    </mat-dialog-actions>
  `,
})
export class TeacherDeactivateDialogComponent {
  protected readonly data = inject<TeacherDeactivateDialogData>(MAT_DIALOG_DATA);
}
