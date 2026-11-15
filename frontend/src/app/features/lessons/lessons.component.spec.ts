import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { LessonResponse } from './lesson.models';
import { LessonService } from './lesson.service';
import { LessonsComponent } from './lessons.component';

describe('LessonsComponent', () => {
  let fixture: ComponentFixture<LessonsComponent>;

  let lessonService: {
    getLessons: ReturnType<typeof vi.fn>;
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
    };

    lessonService.getLessons.mockReturnValue(of([lesson]));

    await TestBed.configureTestingModule({
      imports: [LessonsComponent],
      providers: [
        provideRouter([]),
        {
          provide: LessonService,
          useValue: lessonService,
        },
      ],
    }).compileComponents();
  });

  it('should load and show lessons', () => {
    fixture = TestBed.createComponent(LessonsComponent);
    fixture.detectChanges();

    expect(lessonService.getLessons).toHaveBeenCalledTimes(1);

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Java Basics');
    expect(content).toContain('2026-11-04');
    expect(content).toContain('15:30:00');
    expect(content).toContain('16:30:00');
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
});
