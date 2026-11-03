import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { TeacherResponse } from '../teacher.models';
import { TeacherService } from '../teacher.service';
import { TeacherCreateComponent } from './teacher-create.component';

describe('TeacherCreateComponent', () => {
  let fixture: ComponentFixture<TeacherCreateComponent>;
  let component: TeacherCreateComponent;

  const createdTeacher: TeacherResponse = {
    id: 1,
    firstName: 'Alice',
    lastName: 'Brown',
    email: 'alice.brown@example.com',
  };

  const teacherService = {
    createTeacher: vi.fn(),
  };

  const router = {
    navigateByUrl: vi.fn(),
  };

  beforeEach(async () => {
    teacherService.createTeacher.mockReset();
    router.navigateByUrl.mockReset();

    teacherService.createTeacher.mockReturnValue(of(createdTeacher));
    router.navigateByUrl.mockResolvedValue(true);

    await TestBed.configureTestingModule({
      imports: [TeacherCreateComponent],
      providers: [
        { provide: TeacherService, useValue: teacherService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TeacherCreateComponent);
    component = fixture.componentInstance;
  });

  it('should not create teacher when form is invalid', () => {
    fixture.detectChanges();

    component['submit']();

    expect(teacherService.createTeacher).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should create teacher with trimmed values and navigate to teachers page', () => {
    fixture.detectChanges();

    component['form'].setValue({
      firstName: ' Alice ',
      lastName: ' Brown ',
      email: ' alice.brown@example.com ',
    });

    component['submit']();

    expect(teacherService.createTeacher).toHaveBeenCalledWith({
      firstName: 'Alice',
      lastName: 'Brown',
      email: 'alice.brown@example.com',
    });

    expect(router.navigateByUrl).toHaveBeenCalledWith('/teachers');
  });

  it('should show validation error when request is rejected as invalid', () => {
    teacherService.createTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
          }),
      ),
    );

    fixture.detectChanges();

    component['form'].setValue({
      firstName: 'Alice',
      lastName: 'Brown',
      email: 'alice.brown@example.com',
    });

    component['submit']();

    expect(component['submitError']()).toBe('Please check the entered teacher data.');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should show forbidden error when user cannot create teachers', () => {
    teacherService.createTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture.detectChanges();

    component['form'].setValue({
      firstName: 'Alice',
      lastName: 'Brown',
      email: 'alice.brown@example.com',
    });

    component['submit']();

    expect(component['submitError']()).toBe('You are not allowed to create teachers.');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should show conflict error when email already exists', () => {
    teacherService.createTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
          }),
      ),
    );

    fixture.detectChanges();

    component['form'].setValue({
      firstName: 'Alice',
      lastName: 'Brown',
      email: 'alice.brown@example.com',
    });

    component['submit']();

    expect(component['submitError']()).toBe('A teacher with this email already exists.');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should navigate back to teachers page when cancelled', () => {
    fixture.detectChanges();

    component['cancel']();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/teachers');
    expect(teacherService.createTeacher).not.toHaveBeenCalled();
  });
});
