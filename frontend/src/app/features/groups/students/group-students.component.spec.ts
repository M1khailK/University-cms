import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { GroupService } from '../group.service';
import { GroupStudentsComponent } from './group-students.component';

describe('GroupStudentsComponent', () => {
  const paramMapGet = vi.fn();

  const groupServiceMock = {
    getGroup: vi.fn(),
    getGroupStudents: vi.fn(),
  };

  beforeEach(async () => {
    paramMapGet.mockReset();
    groupServiceMock.getGroup.mockReset();
    groupServiceMock.getGroupStudents.mockReset();

    paramMapGet.mockReturnValue('7');

    groupServiceMock.getGroup.mockReturnValue(
      of({
        id: 7,
        name: 'Java-01',
      }),
    );

    groupServiceMock.getGroupStudents.mockReturnValue(
      of([
        {
          id: 3,
          firstName: 'Zoe',
          lastName: 'Brown',
          email: 'zoe.brown@example.com',
        },
        {
          id: 1,
          firstName: 'Carl',
          lastName: 'Adams',
          email: 'carl.adams@example.com',
        },
        {
          id: 2,
          firstName: 'Alice',
          lastName: 'Brown',
          email: 'alice.brown@example.com',
        },
      ]),
    );

    await TestBed.configureTestingModule({
      imports: [GroupStudentsComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: paramMapGet,
              },
            },
          },
        },
        {
          provide: GroupService,
          useValue: groupServiceMock,
        },
      ],
    }).compileComponents();
  });

  it('should load the group and sort its students by name', () => {
    const fixture = TestBed.createComponent(GroupStudentsComponent);

    fixture.detectChanges();

    expect(groupServiceMock.getGroup).toHaveBeenCalledWith(7);
    expect(groupServiceMock.getGroupStudents).toHaveBeenCalledWith(7);
    expect(fixture.componentInstance['group']()).toEqual({
      id: 7,
      name: 'Java-01',
    });
    expect(
      fixture.componentInstance['students']().map((student) => ({
        firstName: student.firstName,
        lastName: student.lastName,
      })),
    ).toEqual([
      {
        firstName: 'Carl',
        lastName: 'Adams',
      },
      {
        firstName: 'Alice',
        lastName: 'Brown',
      },
      {
        firstName: 'Zoe',
        lastName: 'Brown',
      },
    ]);
  });

  it('should show an empty state when the group has no students', () => {
    groupServiceMock.getGroupStudents.mockReturnValue(of([]));

    const fixture = TestBed.createComponent(GroupStudentsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('Java-01');
    expect(content).toContain('No students assigned');
    expect(content).toContain('This group does not have any students yet.');
  });

  it('should reject an invalid group id without calling the service', () => {
    paramMapGet.mockReturnValue('invalid');

    const fixture = TestBed.createComponent(GroupStudentsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(groupServiceMock.getGroup).not.toHaveBeenCalled();
    expect(groupServiceMock.getGroupStudents).not.toHaveBeenCalled();
    expect(content).toContain('The group address is invalid.');
  });

  it('should explain when administrator permissions are missing', () => {
    groupServiceMock.getGroupStudents.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    const fixture = TestBed.createComponent(GroupStudentsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('Students unavailable');
    expect(content).toContain('Administrator permissions are required to view group students.');
  });
});
