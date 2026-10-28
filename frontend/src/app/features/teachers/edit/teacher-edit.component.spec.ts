import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { convertToParamMap, ActivatedRoute, Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { TeacherResponse, TeacherUpdateRequest } from '../teacher.models';
import { TeacherService } from '../teacher.service';
import { TeacherEditComponent } from './teacher-edit.component';

describe('TeacherEditComponent', () => {
  let fixture: ComponentFixture<TeacherEditComponent>;
  let component: TeacherEditComponent;
  let router: Router;

  const teacher: TeacherResponse = {
    id: 7,
    firstName: 'John',
    lastName: 'Smith',
    email: 'john.smith@example.com',
  };

  const teacherService = {
    getTeacher: vi.fn(),
    updateTeacher: vi.fn(),
  };

  async function createComponent(routeId = '7'): Promise<void> {
    await TestBed.configureTestingModule({
      imports: [TeacherEditComponent],
      providers: [
        provideRouter([]),
        {
          provide: TeacherService,
          useValue: teacherService,
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                id: routeId,
              }),
            },
          },
        },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(TeacherEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  beforeEach(() => {
    TestBed.resetTestingModule();
    vi.clearAllMocks();

    teacherService.getTeacher.mockReturnValue(of(teacher));
    teacherService.updateTeacher.mockReturnValue(of(teacher));
  });

  it('should load teacher and populate the form', async () => {
    await createComponent();

    expect(teacherService.getTeacher).toHaveBeenCalledWith(7);

    expect(component.form.getRawValue()).toEqual({
      firstName: 'John',
      lastName: 'Smith',
      email: 'john.smith@example.com',
    });

    expect(component.loading()).toBe(false);
    expect(component.loadError()).toBeNull();
  });

  it('should show not found error when teacher does not exist', async () => {
    teacherService.getTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
          }),
      ),
    );

    await createComponent();

    expect(component.loading()).toBe(false);
    expect(component.loadError()).toBe('Teacher not found.');
  });

  it('should not request teacher when route id is invalid', async () => {
    await createComponent('invalid');

    expect(teacherService.getTeacher).not.toHaveBeenCalled();
    expect(component.loading()).toBe(false);
    expect(component.loadError()).toBe('Teacher not found.');
  });

  it('should not update teacher when form is invalid', async () => {
    await createComponent();

    component.form.setValue({
      firstName: '   ',
      lastName: '',
      email: 'invalid-email',
    });

    component.submit();

    expect(teacherService.updateTeacher).not.toHaveBeenCalled();
  });

  it('should trim values, update teacher and navigate to teachers', async () => {
    await createComponent();

    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.form.setValue({
      firstName: '  Michael  ',
      lastName: '  Brown  ',
      email: '  michael.brown@example.com  ',
    });

    component.submit();

    const expectedRequest: TeacherUpdateRequest = {
      firstName: 'Michael',
      lastName: 'Brown',
      email: 'michael.brown@example.com',
    };

    expect(teacherService.updateTeacher).toHaveBeenCalledWith(7, expectedRequest);

    expect(component.form.getRawValue()).toEqual(expectedRequest);

    expect(navigateSpy).toHaveBeenCalledWith(['/teachers']);
  });

  it('should show conflict error when email already exists', async () => {
    teacherService.updateTeacher.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
          }),
      ),
    );

    await createComponent();

    component.form.setValue({
      firstName: 'John',
      lastName: 'Smith',
      email: 'existing@example.com',
    });

    component.submit();

    expect(component.submitError()).toBe('A teacher with this email already exists.');
  });

  it('should navigate back to teachers when cancel is called', async () => {
    await createComponent();

    const navigateSpy = vi.spyOn(router, 'navigate').mockResolvedValue(true);

    component.cancel();

    expect(navigateSpy).toHaveBeenCalledWith(['/teachers']);
  });
});
