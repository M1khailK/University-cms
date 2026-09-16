import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { GroupService } from '../../groups/group.service';
import { StudentResponse } from '../student.models';
import { StudentService } from '../student.service';
import { StudentCreateComponent } from './student-create.component';

describe('StudentCreateComponent', () => {
  let fixture: ComponentFixture<StudentCreateComponent>;
  let component: StudentCreateComponent;

  const groups = [
    {
      id: 10,
      name: 'Java-01',
    },
    {
      id: 20,
      name: 'Java-02',
    },
  ];

  const createdStudent: StudentResponse = {
    id: 1,
    firstName: 'Alice',
    lastName: 'Stone',
    email: 'alice@example.com',
    groupId: 10,
    groupName: 'Java-01',
  };

  const studentService = {
    createStudent: vi.fn(),
  };

  const groupService = {
    getGroups: vi.fn(),
  };

  const router = {
    navigateByUrl: vi.fn(),
  };

  beforeEach(async () => {
    studentService.createStudent.mockReset();
    groupService.getGroups.mockReset();
    router.navigateByUrl.mockReset();

    groupService.getGroups.mockReturnValue(of(groups));
    studentService.createStudent.mockReturnValue(of(createdStudent));
    router.navigateByUrl.mockResolvedValue(true);

    await TestBed.configureTestingModule({
      imports: [StudentCreateComponent],
      providers: [
        { provide: StudentService, useValue: studentService },
        { provide: GroupService, useValue: groupService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentCreateComponent);
    component = fixture.componentInstance;
  });

  it('should load groups on initialization', () => {
    fixture.detectChanges();

    expect(groupService.getGroups).toHaveBeenCalledOnce();
  });

  it('should not create student when form is invalid', () => {
    fixture.detectChanges();

    component['submit']();

    expect(studentService.createStudent).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should create student and navigate to students page when form is valid', () => {
    fixture.detectChanges();

    component['form'].setValue({
      firstName: ' Alice ',
      lastName: ' Stone ',
      email: ' alice@example.com ',
      groupId: 10,
    });

    component['submit']();

    expect(studentService.createStudent).toHaveBeenCalledWith({
      firstName: 'Alice',
      lastName: 'Stone',
      email: 'alice@example.com',
      groupId: 10,
    });

    expect(router.navigateByUrl).toHaveBeenCalledWith('/students');
  });

  it('should show conflict message when email already exists', () => {
    studentService.createStudent.mockReturnValue(
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
      lastName: 'Stone',
      email: 'alice@example.com',
      groupId: 10,
    });

    component['submit']();

    expect(component['submitError']()).toBe('A student with this email already exists.');
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
