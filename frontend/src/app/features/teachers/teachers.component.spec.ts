import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { TeacherResponse } from './teacher.models';
import { TeacherService } from './teacher.service';
import { TeachersComponent } from './teachers.component';

describe('TeachersComponent', () => {
  let fixture: ComponentFixture<TeachersComponent>;
  let teacherService: {
    getTeachers: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    teacherService = {
      getTeachers: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TeachersComponent],
      providers: [{ provide: TeacherService, useValue: teacherService }],
    }).compileComponents();
  });

  it('should show teachers', () => {
    const teachers: TeacherResponse[] = [
      {
        id: 1,
        firstName: 'Bob',
        lastName: 'Smith',
        email: 'bob.smith@example.com',
      },
    ];

    teacherService.getTeachers.mockReturnValue(of(teachers));

    fixture = TestBed.createComponent(TeachersComponent);
    fixture.detectChanges();

    const content = fixture.nativeElement.textContent as string;

    expect(content).toContain('Bob');
    expect(content).toContain('Smith');
    expect(content).toContain('bob.smith@example.com');
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
});
