import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { GradeOptionsResponse, GradeResponse } from '../grade.models';
import { GradeService } from '../grade.service';
import { GradeCreateComponent } from './grade-create.component';

describe('GradeCreateComponent', () => {
  let fixture: ComponentFixture<GradeCreateComponent>;
  let component: GradeCreateComponent;

  let gradeService: {
    getGradeCreationOptions: ReturnType<typeof vi.fn>;
    createGrade: ReturnType<typeof vi.fn>;
  };

  let router: {
    navigateByUrl: ReturnType<typeof vi.fn>;
  };

  const options: GradeOptionsResponse = {
    lessons: [
      {
        id: 100,
        name: 'Java Basics',
        date: '2026-11-15',
        startTime: '10:00:00',
        endTime: '11:30:00',
        groupId: 10,
        groupName: 'AA-11',
        students: [
          {
            id: 1,
            firstName: 'Alice',
            lastName: 'Brown',
            email: 'alice.brown@example.com',
          },
        ],
      },
      {
        id: 200,
        name: 'Spring',
        date: '2026-11-16',
        startTime: '12:00:00',
        endTime: '13:30:00',
        groupId: 20,
        groupName: 'BB-22',
        students: [
          {
            id: 2,
            firstName: 'John',
            lastName: 'Smith',
            email: 'john.smith@example.com',
          },
        ],
      },
    ],
  };

  const createdGrade: GradeResponse = {
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
    gradeService = {
      getGradeCreationOptions: vi.fn().mockReturnValue(of(options)),
      createGrade: vi.fn().mockReturnValue(of(createdGrade)),
    };

    router = {
      navigateByUrl: vi.fn().mockResolvedValue(true),
    };

    await TestBed.configureTestingModule({
      imports: [GradeCreateComponent],
      providers: [
        {
          provide: GradeService,
          useValue: gradeService,
        },
        {
          provide: Router,
          useValue: router,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(GradeCreateComponent);
    component = fixture.componentInstance;
  });

  it('should load grade creation options', () => {
    fixture.detectChanges();

    expect(gradeService.getGradeCreationOptions).toHaveBeenCalledTimes(1);

    expect(component['lessons']()).toEqual(options.lessons);
  });

  it('should reset student when lesson changes', () => {
    fixture.detectChanges();

    component['form'].controls.lessonId.setValue(100);
    component['form'].controls.studentId.setValue(1);

    component['form'].controls.lessonId.setValue(200);

    expect(component['form'].controls.studentId.value).toBeNull();
    expect(component['studentsForSelectedLesson']()).toEqual(options.lessons[1].students);
  });

  it('should not submit invalid form', () => {
    fixture.detectChanges();

    component['submit']();

    expect(gradeService.createGrade).not.toHaveBeenCalled();
    expect(component['form'].touched).toBe(true);
  });

  it('should create grade and navigate to grades', () => {
    fixture.detectChanges();

    component['form'].controls.lessonId.setValue(100);
    component['form'].controls.studentId.setValue(1);
    component['form'].controls.value.setValue(5);

    component['submit']();

    expect(gradeService.createGrade).toHaveBeenCalledWith({
      lessonId: 100,
      studentId: 1,
      value: 5,
    });

    expect(router.navigateByUrl).toHaveBeenCalledWith('/grades');
  });

  it('should navigate to grades when cancelled', () => {
    fixture.detectChanges();

    component['cancel']();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/grades');
  });

  it('should show options loading error', () => {
    gradeService.getGradeCreationOptions.mockReturnValue(
      throwError(() => new Error('Request failed')),
    );

    fixture.detectChanges();

    expect(component['errorMessage']()).toBe('Failed to load grade options.');
  });

  it.each([
    [400, 'Grade data is invalid.'],
    [403, 'You are not allowed to create grades.'],
    [404, 'Selected lesson or student was not found.'],
    [409, 'Selected student does not belong to the lesson group.'],
  ])('should show expected error for HTTP %i', (status: number, expectedMessage: string) => {
    gradeService.createGrade.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status,
          }),
      ),
    );

    fixture.detectChanges();

    component['form'].controls.lessonId.setValue(100);
    component['form'].controls.studentId.setValue(1);
    component['form'].controls.value.setValue(5);

    component['submit']();

    expect(component['errorMessage']()).toBe(expectedMessage);
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
