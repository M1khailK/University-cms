import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';

import { LessonResponse } from './lesson.models';
import { LessonService } from './lesson.service';
import { LessonsComponent } from './lessons.component';

describe('LessonsComponent', () => {
  let fixture: ComponentFixture<LessonsComponent>;

  let lessonService: {
    getLessons: ReturnType<typeof vi.fn>;
    deleteLesson: ReturnType<typeof vi.fn>;
  };

  const dialog = {
    open: vi.fn(),
  };

  const lesson: LessonResponse = {
    id: 1,
    name: 'Java Basics',
    date: '2026-11-04',
    startTime: '15:30:00',
    endTime: '16:30:00',
    subjectId: 10,
    subjectName: 'Java',
    groupId: 20,
    groupName: 'AA-01',
    teacherId: 30,
    teacherFirstName: 'Bob',
    teacherLastName: 'Smith',
    teacherEmail: 'bob.smith@example.com',
  };

  beforeEach(async () => {
    lessonService = {
      getLessons: vi.fn(),
      deleteLesson: vi.fn(),
    };

    lessonService.getLessons.mockReturnValue(of([lesson]));

    TestBed.configureTestingModule({
      imports: [LessonsComponent],
      providers: [
        provideRouter([]),
        {
          provide: LessonService,
          useValue: lessonService,
        },
      ],
    });

    TestBed.overrideProvider(MatDialog, {
      useValue: dialog,
    });

    await TestBed.compileComponents();
  });

  it('should load and show lessons', () => {
    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    expect(lessonService.getLessons).toHaveBeenCalledTimes(1);

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Java Basics');
    expect(content).toContain('2026-11-04');
    expect(content).toContain('15:30');
    expect(content).toContain('16:30');
    expect(content).not.toContain('15:30:00');
    expect(content).not.toContain('16:30:00');
    expect(content).toContain('Java');
    expect(content).toContain('AA-01');
    expect(content).toContain('Bob');
    expect(content).toContain('Smith');
  });

  it('should show empty state when no lessons exist', () => {
    lessonService.getLessons.mockReturnValue(of([]));

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No lessons found.');
  });

  it('should show error when lessons cannot be loaded', () => {
    lessonService.getLessons.mockReturnValue(throwError(() => new Error('Request failed')));

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Failed to load lessons.');
  });

  it('should show loading state while lessons are loading', () => {
    const response$ = new Subject<LessonResponse[]>();

    lessonService.getLessons.mockReturnValue(response$);

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Loading lessons...');

    response$.complete();
  });

  it('should render edit link for lesson', () => {
    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    const editLink = fixture.nativeElement.querySelector(
      'a[href="/lessons/1/edit"]',
    ) as HTMLAnchorElement | null;

    expect(editLink).not.toBeNull();
  });

  it('should not delete lesson when confirmation is cancelled', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteLesson'](lesson);

    expect(lessonService.deleteLesson).not.toHaveBeenCalled();
  });

  it('should delete lesson and reload lessons after confirmation', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    lessonService.deleteLesson.mockReturnValue(of(undefined));

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteLesson'](lesson);

    expect(lessonService.deleteLesson).toHaveBeenCalledWith(1);
    expect(lessonService.getLessons).toHaveBeenCalledTimes(2);
  });

  it('should show forbidden error when lesson cannot be deleted', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    lessonService.deleteLesson.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteLesson'](lesson);

    expect(fixture.componentInstance['actionError']()).toBe(
      'You are not allowed to delete lessons.',
    );
  });

  it('should show not found error when lesson does not exist', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    lessonService.deleteLesson.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
          }),
      ),
    );

    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteLesson'](lesson);

    expect(fixture.componentInstance['actionError']()).toBe('Lesson was not found.');
  });
});
