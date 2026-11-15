import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { GroupService } from '../../groups/group.service';
import { SubjectService } from '../../subjects/subject.service';
import { TeacherService } from '../../teachers/teacher.service';
import { LessonResponse } from '../lesson.models';
import { LessonService } from '../lesson.service';
import { LessonEditComponent } from './lesson-edit.component';

describe('LessonEditComponent', () => {
  let fixture: ComponentFixture<LessonEditComponent>;
  let component: LessonEditComponent;

  let lessonService: {
    getLesson: ReturnType<typeof vi.fn>;
    updateLesson: ReturnType<typeof vi.fn>;
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

  const lesson: LessonResponse = {
    id: 1,
    name: 'Java Basics',
    date: '2026-11-15',
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
      getLesson: vi.fn().mockReturnValue(of(lesson)),
      updateLesson: vi.fn().mockReturnValue(of(lesson)),
    };

    subjectService = {
      getSubjects: vi.fn().mockReturnValue(
        of([
          {
            id: 10,
            name: 'Java',
          },
        ]),
      ),
    };

    groupService = {
      getGroups: vi.fn().mockReturnValue(
        of([
          {
            id: 20,
            name: 'AA-01',
          },
        ]),
      ),
    };

    teacherService = {
      getTeachers: vi.fn().mockReturnValue(
        of([
          {
            id: 30,
            firstName: 'Bob',
            lastName: 'Smith',
            email: 'bob.smith@example.com',
          },
        ]),
      ),
    };

    router = {
      navigateByUrl: vi.fn().mockResolvedValue(true),
    };

    await TestBed.configureTestingModule({
      imports: [LessonEditComponent],
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
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                id: '1',
              }),
            },
          },
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LessonEditComponent);
    component = fixture.componentInstance;
  });

  it('should load lesson and options', () => {
    fixture.detectChanges();

    expect(lessonService.getLesson).toHaveBeenCalledWith(1);
    expect(subjectService.getSubjects).toHaveBeenCalledTimes(1);
    expect(groupService.getGroups).toHaveBeenCalledTimes(1);
    expect(teacherService.getTeachers).toHaveBeenCalledTimes(1);

    expect(component['form'].getRawValue()).toEqual({
      name: 'Java Basics',
      date: '2026-11-15',
      startTime: '15:30',
      endTime: '16:30',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });
  });

  it('should reject invalid lesson id', async () => {
    await TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [LessonEditComponent],
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
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({
                id: 'abc',
              }),
            },
          },
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    }).compileComponents();

    const invalidFixture = TestBed.createComponent(LessonEditComponent);
    const invalidComponent = invalidFixture.componentInstance;

    invalidFixture.detectChanges();

    expect(invalidComponent['errorMessage']()).toBe('Invalid lesson id.');
    expect(lessonService.getLesson).not.toHaveBeenCalled();
  });

  it('should reject end time that is not after start time', () => {
    fixture.detectChanges();

    component['form'].patchValue({
      startTime: '17:00',
      endTime: '16:00',
    });

    expect(component['form'].hasError('invalidTimeRange')).toBe(true);
    expect(component['form'].valid).toBe(false);

    component['submit']();

    expect(lessonService.updateLesson).not.toHaveBeenCalled();
  });

  it('should update lesson with normalized time and trimmed name', () => {
    fixture.detectChanges();

    component['form'].setValue({
      name: '  Advanced Java  ',
      date: '2026-11-16',
      startTime: '10:15',
      endTime: '11:45',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });

    component['submit']();

    expect(lessonService.updateLesson).toHaveBeenCalledWith(1, {
      name: 'Advanced Java',
      date: '2026-11-16',
      startTime: '10:15:00',
      endTime: '11:45:00',
      subjectId: 10,
      groupId: 20,
      teacherId: 30,
    });
  });

  it('should navigate to lessons after successful update', () => {
    fixture.detectChanges();

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
    [403, 'You are not allowed to edit lessons.'],
    [404, 'Lesson, subject, group, or teacher was not found.'],
  ])('should show expected update error for HTTP %i', (status: number, expectedMessage: string) => {
    lessonService.updateLesson.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status,
          }),
      ),
    );

    fixture.detectChanges();

    component['submit']();

    expect(component['errorMessage']()).toBe(expectedMessage);
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it.each([
    [403, 'You are not allowed to edit lessons.'],
    [404, 'Lesson was not found.'],
  ])('should show expected load error for HTTP %i', (status: number, expectedMessage: string) => {
    lessonService.getLesson.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status,
          }),
      ),
    );

    fixture.detectChanges();

    expect(component['errorMessage']()).toBe(expectedMessage);
  });
});
