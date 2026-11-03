import { Component, inject, OnInit, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { LessonResponse } from './lesson.models';
import { LessonService } from './lesson.service';

@Component({
  selector: 'app-lessons',
  imports: [MatButtonModule, MatTableModule, RouterLink],
  templateUrl: './lessons.component.html',
  styleUrl: './lessons.component.scss',
})
export class LessonsComponent implements OnInit {
  private readonly lessonService = inject(LessonService);

  protected readonly displayedColumns = ['name', 'date', 'time', 'subject', 'group', 'teacher'];

  protected readonly lessons = signal<LessonResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadLessons();
  }

  private loadLessons(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.lessonService
      .getLessons()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (lessons) => {
          this.lessons.set(lessons);
        },
        error: () => {
          this.errorMessage.set('Failed to load lessons.');
        },
      });
  }
}
