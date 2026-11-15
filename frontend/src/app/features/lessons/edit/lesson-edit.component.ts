import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { GroupResponse } from '../../groups/group.models';
import { GroupService } from '../../groups/group.service';
import { SubjectResponse } from '../../subjects/subject.models';
import { SubjectService } from '../../subjects/subject.service';
import { TeacherResponse } from '../../teachers/teacher.models';
import { TeacherService } from '../../teachers/teacher.service';
import { LessonUpdateRequest } from '../lesson.models';
import { LessonService } from '../lesson.service';

@Component({
  selector: 'app-lesson-edit',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './lesson-edit.component.html',
  styleUrl: './lesson-edit.component.scss',
})
export class LessonEditComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly lessonService = inject(LessonService);
  private readonly subjectService = inject(SubjectService);
  private readonly groupService = inject(GroupService);
  private readonly teacherService = inject(TeacherService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected lessonId: number | null = null;

  protected readonly subjects = signal<SubjectResponse[]>([]);
  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly teachers = signal<TeacherResponse[]>([]);

  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.formBuilder.group(
    {
      name: ['', [Validators.required, Validators.maxLength(50), this.notBlankValidator]],
      date: ['', Validators.required],
      startTime: ['', Validators.required],
      endTime: ['', Validators.required],
      subjectId: [null as number | null, [Validators.required, Validators.min(1)]],
      groupId: [null as number | null, [Validators.required, Validators.min(1)]],
      teacherId: [null as number | null, [Validators.required, Validators.min(1)]],
    },
    {
      validators: this.timeRangeValidator,
    },
  );

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.errorMessage.set('Invalid lesson id.');
      return;
    }

    this.lessonId = id;
    this.loadLesson(id);
  }

  protected submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid || this.lessonId === null) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();

    const request: LessonUpdateRequest = {
      name: value.name!.trim(),
      date: value.date!,
      startTime: this.normalizeTime(value.startTime!),
      endTime: this.normalizeTime(value.endTime!),
      subjectId: value.subjectId!,
      groupId: value.groupId!,
      teacherId: value.teacherId!,
    };

    this.submitting.set(true);

    this.lessonService
      .updateLesson(this.lessonId, request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/lessons');
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getUpdateErrorMessage(error));
        },
      });
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/lessons');
  }

  private loadLesson(id: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      lesson: this.lessonService.getLesson(id),
      subjects: this.subjectService.getSubjects(),
      groups: this.groupService.getGroups(),
      teachers: this.teacherService.getTeachers(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ lesson, subjects, groups, teachers }) => {
          this.subjects.set(subjects);
          this.groups.set(groups);
          this.teachers.set(teachers);

          this.form.setValue({
            name: lesson.name,
            date: lesson.date,
            startTime: this.toTimeInputValue(lesson.startTime),
            endTime: this.toTimeInputValue(lesson.endTime),
            subjectId: lesson.subjectId,
            groupId: lesson.groupId,
            teacherId: lesson.teacherId,
          });
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getLoadErrorMessage(error));
        },
      });
  }

  private toTimeInputValue(time: string): string {
    return time.slice(0, 5);
  }

  private normalizeTime(time: string): string {
    return time.length === 5 ? `${time}:00` : time;
  }

  private getLoadErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to load lesson.';
    }

    if (error.status === 403) {
      return 'You are not allowed to edit lessons.';
    }

    if (error.status === 404) {
      return 'Lesson was not found.';
    }

    return 'Failed to load lesson.';
  }

  private getUpdateErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to update lesson.';
    }

    if (error.status === 400) {
      return 'Lesson data is invalid.';
    }

    if (error.status === 403) {
      return 'You are not allowed to edit lessons.';
    }

    if (error.status === 404) {
      return 'Lesson, subject, group, or teacher was not found.';
    }

    return 'Failed to update lesson.';
  }

  private notBlankValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;

    if (typeof value !== 'string') {
      return null;
    }

    return value.trim().length === 0 ? { blank: true } : null;
  }

  private timeRangeValidator(control: AbstractControl): ValidationErrors | null {
    const startTime = control.get('startTime')?.value;
    const endTime = control.get('endTime')?.value;

    if (!startTime || !endTime) {
      return null;
    }

    return endTime > startTime ? null : { invalidTimeRange: true };
  }
}
