import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { AuthService } from '../auth/auth.service';
import { SubjectResponse } from './subject.models';
import { SubjectService } from './subject.service';
import { SubjectsComponent } from './subjects.component';

describe('SubjectsComponent', () => {
  const initialSubjects: SubjectResponse[] = [
    {
      id: 2,
      name: 'Software Architecture',
    },
    {
      id: 1,
      name: 'Biology',
    },
  ];

  const isAuthenticated = signal(false);
  const isAdmin = signal(false);

  const subjectServiceMock = {
    getSubjects: vi.fn(),
    createSubject: vi.fn(),
    updateSubject: vi.fn(),
    deleteSubject: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    isAuthenticated.set(false);
    isAdmin.set(false);

    subjectServiceMock.getSubjects.mockReset();
    subjectServiceMock.createSubject.mockReset();
    subjectServiceMock.updateSubject.mockReset();
    subjectServiceMock.deleteSubject.mockReset();
    dialogMock.open.mockReset();

    subjectServiceMock.getSubjects.mockReturnValue(of(initialSubjects));

    TestBed.configureTestingModule({
      imports: [SubjectsComponent],
      providers: [
        provideRouter([]),
        {
          provide: SubjectService,
          useValue: subjectServiceMock,
        },
        {
          provide: AuthService,
          useValue: {
            isAuthenticated,
            isAdmin,
          },
        },
      ],
    });

    TestBed.overrideProvider(MatDialog, {
      useValue: dialogMock,
    });

    await TestBed.compileComponents();
  });

  it('should load, sort and filter public subjects', () => {
    const fixture = TestBed.createComponent(SubjectsComponent);

    fixture.detectChanges();

    expect(fixture.componentInstance['subjects']().map((subject) => subject.name)).toEqual([
      'Biology',
      'Software Architecture',
    ]);

    fixture.componentInstance['updateQuery'](inputEvent('software'));

    expect(fixture.componentInstance['filteredSubjects']()).toEqual([
      {
        id: 2,
        name: 'Software Architecture',
      },
    ]);
  });

  it('should hide write controls from guests', () => {
    const fixture = TestBed.createComponent(SubjectsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('University subjects');
    expect(content).toContain('Sign in');
    expect(content).not.toContain('Add subject');
    expect(content).not.toContain('Edit');
    expect(content).not.toContain('Delete');
  });

  it('should create a subject as administrator', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    dialogMock.open.mockReturnValue({
      afterClosed: () =>
        of({
          name: 'Mathematics',
        }),
    });

    subjectServiceMock.createSubject.mockReturnValue(
      of({
        id: 3,
        name: 'Mathematics',
      }),
    );

    const fixture = TestBed.createComponent(SubjectsComponent);

    fixture.detectChanges();

    fixture.componentInstance['openCreateDialog']();

    expect(subjectServiceMock.createSubject).toHaveBeenCalledWith({
      name: 'Mathematics',
    });

    expect(fixture.componentInstance['subjects']()).toContainEqual({
      id: 3,
      name: 'Mathematics',
    });
  });

  it('should delete a confirmed subject', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    subjectServiceMock.deleteSubject.mockReturnValue(of(undefined));

    const fixture = TestBed.createComponent(SubjectsComponent);

    fixture.detectChanges();

    const subject = fixture.componentInstance['subjects']()[0];

    fixture.componentInstance['requestDelete'](subject.id);

    fixture.componentInstance['deleteSubject'](subject);

    expect(subjectServiceMock.deleteSubject).toHaveBeenCalledWith(subject.id);

    expect(fixture.componentInstance['subjects']()).not.toContainEqual(subject);
  });
});

function inputEvent(value: string): Event {
  return {
    target: {
      value,
    },
  } as unknown as Event;
}
