import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { GradeResponse } from './grade.models';
import { GradeService } from './grade.service';
import { GradesComponent } from './grades.component';
import { MatDialog } from '@angular/material/dialog';
describe('GradesComponent', () => {
  let fixture: ComponentFixture<GradesComponent>;
  let isStudent: ReturnType<typeof signal<boolean>>;
  let isTeacher: ReturnType<typeof signal<boolean>>;
  let gradeService: {
    getGrades: ReturnType<typeof vi.fn>;
    getMyGrades: ReturnType<typeof vi.fn>;
    deleteGrade: ReturnType<typeof vi.fn>;
  };
  const dialog = {
    open: vi.fn(),
  };
  const grade: GradeResponse = {
    id: 500,
    value: 5,
    studentId: 1,
    studentFirstName: 'Alice',
    studentLastName: 'Brown',
    studentEmail: 'alice.brown@example.com',
    lessonId: 100,
    lessonName: 'Java Basics',
    lessonDate: '2026-11-15',
    lessonStartTime: '10:00:00',
    lessonEndTime: '11:30:00',
  };

  beforeEach(async () => {
    dialog.open.mockReset();
    isStudent = signal(false);
    isTeacher = signal(false);
    gradeService = {
      getGrades: vi.fn().mockReturnValue(of([grade])),
      getMyGrades: vi.fn().mockReturnValue(of([grade])),
      deleteGrade: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [GradesComponent],
      providers: [
        provideRouter([]),
        {
          provide: GradeService,
          useValue: gradeService,
        },
        {
          provide: AuthService,
          useValue: {
            isStudent,
            isTeacher,
          },
        },
      ],
    });
    TestBed.overrideProvider(MatDialog, {
      useValue: dialog,
    });
    await TestBed.compileComponents();
  });

  it('should load all grades for a non-student user', () => {
    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(gradeService.getGrades).toHaveBeenCalledTimes(1);
    expect(gradeService.getMyGrades).not.toHaveBeenCalled();

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Alice Brown');
    expect(content).toContain('Java Basics');
    expect(content).toContain('2026-11-15');
    expect(content).toContain('10:00');
    expect(content).toContain('11:30');
    expect(content).toContain('5');
  });

  it('should load only current student grades for a student', () => {
    isStudent.set(true);

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(gradeService.getMyGrades).toHaveBeenCalledTimes(1);
    expect(gradeService.getGrades).not.toHaveBeenCalled();

    expect(fixture.nativeElement.textContent).toContain('My grades');
    expect(fixture.nativeElement.textContent).not.toContain('Alice Brown');
  });

  it('should show empty state when no grades exist', () => {
    gradeService.getGrades.mockReturnValue(of([]));

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No grades found.');
  });

  it('should show loading state while grades are loading', () => {
    gradeService.getGrades.mockReturnValue(new Subject<GradeResponse[]>());

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Loading grades...');
  });

  it('should show forbidden error', () => {
    gradeService.getGrades.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('You are not allowed to view grades.');
  });

  it('should show generic loading error', () => {
    gradeService.getGrades.mockReturnValue(throwError(() => new Error('Request failed')));

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Failed to load grades.');
  });

  it('should show add grade link only for a teacher', () => {
    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a[href="/grades/new"]')).toBeNull();

    isTeacher.set(true);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('a[href="/grades/new"]')).not.toBeNull();
  });

  it('should not delete grade when confirmation is cancelled', () => {
    isTeacher.set(true);

    dialog.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteGrade'](grade);

    expect(gradeService.deleteGrade).not.toHaveBeenCalled();
  });

  it('should delete grade and reload grades after confirmation', () => {
    isTeacher.set(true);

    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    gradeService.deleteGrade.mockReturnValue(of(undefined));

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteGrade'](grade);

    expect(gradeService.deleteGrade).toHaveBeenCalledWith(500);
    expect(gradeService.getGrades).toHaveBeenCalledTimes(2);
  });

  it.each([
    [403, 'You are not allowed to delete this grade.'],
    [404, 'Grade was not found.'],
  ])('should show delete error for HTTP %i', (status: number, expectedMessage: string) => {
    isTeacher.set(true);

    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    gradeService.deleteGrade.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status,
          }),
      ),
    );

    fixture = TestBed.createComponent(GradesComponent);
    fixture.detectChanges();

    fixture.componentInstance['deleteGrade'](grade);

    expect(fixture.componentInstance['actionError']()).toBe(expectedMessage);

    expect(gradeService.getGrades).toHaveBeenCalledTimes(1);
  });
});
