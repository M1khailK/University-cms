import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { finalize } from 'rxjs';
import { TeacherResponse } from './teacher.models';
import { TeacherService } from './teacher.service';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
@Component({
  selector: 'app-teachers',
  imports: [MatButtonModule, MatTableModule, RouterLink],
  templateUrl: './teachers.component.html',
  styleUrl: './teachers.component.scss',
})
export class TeachersComponent implements OnInit {
  private readonly teacherService = inject(TeacherService);

  protected readonly displayedColumns = ['firstName', 'lastName', 'email'];
  protected readonly teachers = signal<TeacherResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadTeachers();
  }

  private loadTeachers(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.teacherService
      .getTeachers()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (teachers) => {
          this.teachers.set(teachers);
        },
        error: () => {
          this.errorMessage.set('Failed to load teachers.');
        },
      });
  }
}
