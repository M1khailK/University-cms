import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';

import { TeacherResponse } from './teacher.models';
import { TeacherService } from './teacher.service';
import { TeachersComponent } from './teachers.component';

describe('TeachersComponent', () => {
  let fixture: ComponentFixture<TeachersComponent>;

  let teacherService: {
    getTeachers: ReturnType<typeof vi.fn>;
    deactivateTeacher: ReturnType<typeof vi.fn>;
  };

  let dialog: {
    open: ReturnType<typeof vi.fn>;
  };

  const teacher: TeacherResponse = {
    id: 1,
    firstName: 'Bob',
    lastName: 'Smith',
    email: 'bob.smith@example.com',
  };

  beforeEach(async () => {
    teacherService = {
      getTeachers: vi.fn(),
      deactivateTeacher: vi.fn(),
    };

    dialog = {
      open: vi.fn(),
    };

    teacherService.getTeachers.mockReturnValue(of([teacher]));
    teacherService.deactivateTeacher.mockReturnValue(of(undefined));

    await TestBed.configureTestingModule({
      imports: [TeachersComponent],
      providers: [
        provideRouter([]),
        {
          provide: TeacherService,
          useValue: teacherService,
        },
      ],
    }).compileComponents();
    TestBed.overrideProvider(MatDialog, {
      useValue: dialog,
    });
  });

  it('should show teachers', () => {
    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Bob');
    expect(content).toContain('Smith');
    expect(content).toContain('bob.smith@example.com');
  });

  it('should render edit link for teacher', () => {
    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    const editLink = fixture.nativeElement.querySelector('a[href="/teachers/1/edit"]');

    expect(editLink).not.toBeNull();
  });

  it('should show empty state when no teachers exist', () => {
    teacherService.getTeachers.mockReturnValue(of([]));

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('No teachers found.');
  });

  it('should show error when teachers cannot be loaded', () => {
    teacherService.getTeachers.mockReturnValue(throwError(() => new Error('Request failed')));

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Failed to load teachers.');
  });

  it('should show loading state while teachers are loading', () => {
    const response$ = new Subject<TeacherResponse[]>();

    teacherService.getTeachers.mockReturnValue(response$);

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Loading teachers...');

    response$.complete();
  });

  it('should not deactivate teacher when confirmation is cancelled', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    fixture.componentInstance['deactivateTeacher'](teacher);

    expect(dialog.open).toHaveBeenCalledTimes(1);
    expect(teacherService.deactivateTeacher).not.toHaveBeenCalled();
    expect(teacherService.getTeachers).toHaveBeenCalledTimes(1);
  });

  it('should deactivate teacher and reload teachers when confirmed', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    teacherService.getTeachers.mockReturnValueOnce(of([teacher])).mockReturnValueOnce(of([]));

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    fixture.componentInstance['deactivateTeacher'](teacher);

    expect(teacherService.deactivateTeacher).toHaveBeenCalledWith(1);
    expect(teacherService.getTeachers).toHaveBeenCalledTimes(2);
  });

  it('should show error when teacher deactivation is forbidden', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    teacherService.deactivateTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    fixture.componentInstance['deactivateTeacher'](teacher);
    fixture.detectChanges();

    expect(fixture.componentInstance['actionError']()).toBe(
      'You are not allowed to deactivate teachers.',
    );

    const errorMessage = fixture.nativeElement.querySelector('.error-message') as HTMLElement;

    expect(errorMessage.textContent?.trim()).toBe('You are not allowed to deactivate teachers.');
  });

  it('should show error when teacher is already inactive', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    teacherService.deactivateTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
          }),
      ),
    );

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    fixture.componentInstance['deactivateTeacher'](teacher);

    expect(fixture.componentInstance['actionError']()).toBe(
      'Teacher was not found or is already inactive.',
    );
  });
});
