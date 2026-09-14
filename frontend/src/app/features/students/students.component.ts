import { Component, inject, OnInit, signal } from '@angular/core';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { StudentResponse } from './student.models';
import { StudentService } from './student.service';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-students',
  imports: [MatButtonModule, MatPaginatorModule, MatTableModule, RouterLink],
  templateUrl: './students.component.html',
  styleUrl: './students.component.scss',
})
export class StudentsComponent implements OnInit {
  private readonly studentService = inject(StudentService);
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
}
