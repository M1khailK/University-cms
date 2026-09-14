import { Component, inject, OnInit, signal } from '@angular/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { filter, finalize, switchMap, tap } from 'rxjs';
import { StudentResponse } from './student.models';
import { StudentService } from './student.service';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { StudentDeactivateDialogComponent } from './deactivate/student-deactivate-dialog.component';
@Component({
  selector: 'app-students',
  imports: [MatButtonModule, MatPaginatorModule, MatTableModule, MatDialogModule, RouterLink],
  templateUrl: './students.component.html',
  styleUrl: './students.component.scss',
})
export class StudentsComponent implements OnInit {
  private readonly studentService = inject(StudentService);
  private readonly dialog = inject(MatDialog);

  protected readonly deactivatingStudentId = signal<number | null>(null);
  protected readonly actionError = signal<string | null>(null);
  protected readonly displayedColumns = [
    'id',
    'firstName',
    'lastName',
    'email',
    'groupName',
    'actions',
  ];
  protected readonly pageIndex = signal(0);
  protected readonly pageSize = signal(20);
  protected readonly totalElements = signal(0);
  protected readonly pageSizeOptions = [10, 20, 50, 100];
  protected readonly students = signal<StudentResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadStudents(this.pageIndex(), this.pageSize());
  }

  protected onPageChange(event: PageEvent): void {
    this.loadStudents(event.pageIndex, event.pageSize);
  }

  protected deactivateStudent(student: StudentResponse): void {
    this.actionError.set(null);

    this.dialog
      .open(StudentDeactivateDialogComponent, {
        data: {
          fullName: `${student.firstName} ${student.lastName}`,
        },
      })
      .afterClosed()
      .pipe(
        filter((confirmed) => confirmed === true),
        tap(() => this.deactivatingStudentId.set(student.id)),
        switchMap(() =>
          this.studentService
            .deactivateStudent(student.id)
            .pipe(finalize(() => this.deactivatingStudentId.set(null))),
        ),
      )
      .subscribe({
        next: () => {
          const targetPage =
            this.students().length === 1 && this.pageIndex() > 0
              ? this.pageIndex() - 1
              : this.pageIndex();

          this.loadStudents(targetPage, this.pageSize());
        },
        error: (error: unknown) => {
          this.actionError.set(this.getDeactivateErrorMessage(error));
        },
      });
  }

  private loadStudents(page: number, size: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.studentService
      .getStudents(page, size)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.students.set(response.content);
          this.pageIndex.set(response.page);
          this.pageSize.set(response.size);
          this.totalElements.set(response.totalElements);
        },
        error: () => {
          this.errorMessage.set('Failed to load students.');
        },
      });
  }
  
  private getDeactivateErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to deactivate student.';
    }

    if (error.status === 403) {
      return 'You are not allowed to deactivate students.';
    }

    if (error.status === 404) {
      return 'Student was not found or is already inactive.';
    }

    return 'Failed to deactivate student.';
  }
}
