import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { GroupService } from '../../groups/group.service';
import { StudentResponse } from '../student.models';
import { StudentService } from '../student.service';
import { StudentEditComponent } from './student-edit.component';

describe('StudentEditComponent', () => {
  let fixture: ComponentFixture<StudentEditComponent>;
  let component: StudentEditComponent;

  let studentService: {
    getStudent: ReturnType<typeof vi.fn>;
    updateStudent: ReturnType<typeof vi.fn>;
  };

  let groupService: {
    getGroups: ReturnType<typeof vi.fn>;
  };

  let router: {
    navigateByUrl: ReturnType<typeof vi.fn>;
  };

  let route: {
    snapshot: {
      paramMap: ReturnType<typeof convertToParamMap>;
    };
  };

  const student: StudentResponse = {
    id: 7,
    firstName: 'Arthur',
    lastName: 'Morgan',
    email: 'arthur.morgan@example.com',
    groupId: 10,
    groupName: 'Exact Sciences Group',
  };

  const groups = [
    {
      id: 10,
      name: 'Exact Sciences Group',
    },
    {
      id: 20,
      name: 'Linguistics Group',
    },
  ];

  beforeEach(async () => {
    studentService = {
      getStudent: vi.fn().mockReturnValue(of(student)),
      updateStudent: vi.fn(),
    };

    groupService = {
      getGroups: vi.fn().mockReturnValue(of(groups)),
    };

    router = {
      navigateByUrl: vi.fn(),
    };

    route = {
      snapshot: {
        paramMap: convertToParamMap({
          id: '7',
        }),
      },
    };

    await TestBed.configureTestingModule({
      imports: [StudentEditComponent],
      providers: [
        {
          provide: StudentService,
          useValue: studentService,
        },
        {
          provide: GroupService,
          useValue: groupService,
        },
        {
          provide: Router,
          useValue: router,
        },
        {
          provide: ActivatedRoute,
          useValue: route,
        },
      ],
    }).compileComponents();
  });

  function createComponent(): void {
    fixture = TestBed.createComponent(StudentEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }

  it('should load student and groups and prefill form on initialization', () => {
    createComponent();

    expect(studentService.getStudent).toHaveBeenCalledWith(7);
    expect(groupService.getGroups).toHaveBeenCalledTimes(1);

    expect(component['groups']()).toEqual(groups);

    expect(component['form'].getRawValue()).toEqual({
      firstName: 'Arthur',
      lastName: 'Morgan',
      email: 'arthur.morgan@example.com',
      groupId: 10,
    });

    expect(component['loadError']()).toBeNull();
    expect(component['loading']()).toBe(false);
  });

  it('should show validation error and skip loading when route id is invalid', () => {
    route.snapshot.paramMap = convertToParamMap({
      id: 'invalid',
    });

    createComponent();

    expect(component['loadError']()).toBe('Invalid student id.');
    expect(studentService.getStudent).not.toHaveBeenCalled();
    expect(groupService.getGroups).not.toHaveBeenCalled();
  });
  it('should update normalized student and navigate to students', () => {
    studentService.updateStudent.mockReturnValue(of(student));

    createComponent();

    component['form'].setValue({
      firstName: '  Arthur  ',
      lastName: '  Morgan  ',
      email: '  arthur.updated@example.com  ',
      groupId: 20,
    });

    component['submit']();

    expect(studentService.updateStudent).toHaveBeenCalledWith(7, {
      firstName: 'Arthur',
      lastName: 'Morgan',
      email: 'arthur.updated@example.com',
      groupId: 20,
    });

    expect(router.navigateByUrl).toHaveBeenCalledWith('/students');
    expect(component['submitting']()).toBe(false);
  });
  it('should not update student when form is invalid', () => {
    createComponent();

    component['form'].patchValue({
      firstName: '   ',
    });

    component['submit']();
    expect(studentService.updateStudent).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
  it('should show duplicate email message when update returns conflict', () => {
    studentService.updateStudent.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 409,
          }),
      ),
    );

    createComponent();

    component['submit']();
    fixture.detectChanges();

    expect(component['submitError']()).toBe('A student with this email already exists.');

    const errorMessage = fixture.nativeElement.querySelector('.error-message') as HTMLElement;

    expect(errorMessage.textContent?.trim()).toBe('A student with this email already exists.');

    expect(router.navigateByUrl).not.toHaveBeenCalled();
    expect(component['submitting']()).toBe(false);
  });
  it('should show not found message when update returns not found', () => {
    studentService.updateStudent.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
          }),
      ),
    );

    createComponent();

    component['submit']();

    expect(component['submitError']()).toBe('Student not found.');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
    expect(component['submitting']()).toBe(false);
  });
});
