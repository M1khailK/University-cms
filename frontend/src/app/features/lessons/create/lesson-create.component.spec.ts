import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { GroupService } from '../../groups/group.service';
import { SubjectService } from '../../subjects/subject.service';
import { TeacherService } from '../../teachers/teacher.service';
import { LessonResponse } from '../lesson.models';
import { LessonService } from '../lesson.service';
import { LessonCreateComponent } from './lesson-create.component';

describe('LessonCreateComponent', () => {
  let fixture: ComponentFixture<LessonCreateComponent>;
  let component: LessonCreateComponent;

  let lessonService: {
    createLesson: ReturnType<typeof vi.fn>;
  };

  let subjectService: {
    getSubjects: ReturnType<typeof vi.fn>;
  };

  let groupService: {
    getGroups: ReturnType<typeof vi.fn>;
  };

  let teacherService: {
    getTeachers: ReturnType<typeof vi.fn>;
  };

  let router: {
    navigateByUrl: ReturnType<typeof vi.fn>;
  };

  const createdLesson: LessonResponse = {
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
      createLesson: vi.fn(),
    };

    subjectService = {
      getSubjects: vi.fn(),
    };

    groupService = {
      getGroups: vi.fn(),
    };

    teacherService = {
      getTeachers: vi.fn(),
    };

    router = {
      navigateByUrl: vi.fn().mockResolvedValue(true),
    };

    subjectService.getSubjects.mockReturnValue(
      of([
        {
          id: 10,
          name: 'Java',
        },
      ]),
    );

    groupService.getGroups.mockReturnValue(
      of([
        {
          id: 20,
          name: 'AA-01',
        },
      ]),
    );

    teacherService.getTeachers.mockReturnValue(
      of([
        {
          id: 30,
          firstName: 'Bob',
          lastName: 'Smith',
          email: 'bob.smith@example.com',
        },
      ]),
    );

    lessonService.createLesson.mockReturnValue(of(createdLesson));

    await TestBed.configureTestingModule({
      imports: [LessonCreateComponent],
      providers: [
        {
          provide: LessonService,
          useValue: lessonService,
        },
        {
          provide: SubjectService,
          useValue: subjectService,
        },
        {
          provide: GroupService,
          useValue: groupService,
        },
        {
          provide: TeacherService,
          useValue: teacherService,
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LessonCreateComponent);
    component = fixture.componentInstance;
  });

  it('should load lesson options', () => {
    fixture.detectChanges();

    expect(subjectService.getSubjects).toHaveBeenCalledTimes(1);
    expect(groupService.getGroups).toHaveBeenCalledTimes(1);
    expect(teacherService.getTeachers).toHaveBeenCalledTimes(1);

    expect(component['subjects']()).toEqual([
      {
        id: 10,
        name: 'Java',
      },
    ]);

    expect(component['groups']()).toEqual([
      {
        id: 20,
        name: 'AA-01',
      },
    ]);

    expect(component['teachers']()).toEqual([
      {
        id: 30,
        firstName: 'Bob',
        lastName: 'Smith',
        email: 'bob.smith@example.com',
      },
    ]);
  });

  it('should show error when lesson options cannot be loaded', () => {
    subjectService.getSubjects.mockReturnValue(throwError(() => new Error('Request failed')));

    fixture.detectChanges();

    expect(component['errorMessage']()).toBe('Failed to load lesson options.');
  });

  it('should not submit invalid form', () => {
    fixture.detectChanges();

    component['submit']();

    expect(lessonService.createLesson).not.toHaveBeenCalled();
    expect(component['form'].touched).toBe(true);
  });

  it('should reject end time that is not after start time', () => {
    fixture.detectChanges();

    component['form'].patchValue({
      name: 'Java Basics',
      date: '2026-11-04',
      startTime: '16:30',
      endTime: '15:30',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });

    expect(component['form'].hasError('invalidTimeRange')).toBe(true);
    expect(component['form'].valid).toBe(false);

    component['submit']();

    expect(lessonService.createLesson).not.toHaveBeenCalled();
  });

  it('should create lesson with trimmed name and normalized time', () => {
    fixture.detectChanges();

    component['form'].setValue({
      name: '  Java Basics  ',
      date: '2026-11-04',
      startTime: '15:30',
      endTime: '16:30',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });

    component['submit']();

    expect(lessonService.createLesson).toHaveBeenCalledTimes(1);

    expect(lessonService.createLesson).toHaveBeenCalledWith({
      name: 'Java Basics',
      date: '2026-11-04',
      startTime: '15:30:00',
      endTime: '16:30:00',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });
  });

  it('should navigate to lessons after successful creation', () => {
    fixture.detectChanges();

    component['form'].setValue({
      name: 'Java Basics',
      date: '2026-11-04',
      startTime: '15:30',
      endTime: '16:30',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });

    component['submit']();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/lessons');
  });

  it('should navigate to lessons when cancel is clicked', () => {
    fixture.detectChanges();

    component['cancel']();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/lessons');
  });

  it.each([
    [400, 'Lesson data is invalid.'],
    [403, 'You are not allowed to create lessons.'],
    [404, 'Subject, group, or teacher was not found.'],
  ])('should show expected error for HTTP %i', (status: number, expectedMessage: string) => {
    lessonService.createLesson.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status,
          }),
      ),
    );

    fixture.detectChanges();

    component['form'].setValue({
      name: 'Java Basics',
      date: '2026-11-04',
      startTime: '15:30',
      endTime: '16:30',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });

    component['submit']();

    expect(component['errorMessage']()).toBe(expectedMessage);
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
