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
import { Router } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';

import { GroupResponse } from '../../groups/group.models';
import { GroupService } from '../../groups/group.service';
import { SubjectResponse } from '../../subjects/subject.models';
import { SubjectService } from '../../subjects/subject.service';
import { TeacherResponse } from '../../teachers/teacher.models';
import { TeacherService } from '../../teachers/teacher.service';
import { LessonCreateRequest } from '../lesson.models';
import { LessonService } from '../lesson.service';

@Component({
  selector: 'app-lesson-create',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './lesson-create.component.html',
  styleUrl: './lesson-create.component.scss',
})
export class LessonCreateComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly lessonService = inject(LessonService);
  private readonly subjectService = inject(SubjectService);
  private readonly groupService = inject(GroupService);
  private readonly teacherService = inject(TeacherService);
  private readonly router = inject(Router);

  protected readonly subjects = signal<SubjectResponse[]>([]);
  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly teachers = signal<TeacherResponse[]>([]);

  protected readonly optionsLoading = signal(false);
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
    this.loadOptions();
  }

  protected submit(): void {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();

    const request: LessonCreateRequest = {
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
      .createLesson(request)
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigateByUrl('/lessons');
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getCreateErrorMessage(error));
        },
      });
  }

  protected cancel(): void {
    void this.router.navigateByUrl('/lessons');
  }

  private loadOptions(): void {
    this.optionsLoading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      subjects: this.subjectService.getSubjects(),
      groups: this.groupService.getGroups(),
      teachers: this.teacherService.getTeachers(),
    })
      .pipe(finalize(() => this.optionsLoading.set(false)))
      .subscribe({
        next: ({ subjects, groups, teachers }) => {
          this.subjects.set(subjects);
          this.groups.set(groups);
          this.teachers.set(teachers);
        },
        error: () => {
          this.errorMessage.set('Failed to load lesson options.');
        },
      });
  }

  private normalizeTime(time: string): string {
    return time.length === 5 ? `${time}:00` : time;
  }

  private getCreateErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to create lesson.';
    }

    if (error.status === 400) {
      return 'Lesson data is invalid.';
    }

    if (error.status === 403) {
      return 'You are not allowed to create lessons.';
    }

    if (error.status === 404) {
      return 'Subject, group, or teacher was not found.';
    }

    return 'Failed to create lesson.';
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
