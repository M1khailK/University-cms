import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ScheduleComponent } from './schedule.component';
import { ScheduleLessonResponse } from './schedule.models';
import { ScheduleService } from './schedule.service';
import { signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
describe('ScheduleComponent', () => {
  let fixture: ComponentFixture<ScheduleComponent>;
  let component: ScheduleComponent;

  const scheduleServiceMock = {
    getMySchedule: vi.fn(),
  };

  const isTeacher = signal(false);

  const authServiceMock = {
    isTeacher,
  };

  const dialogMock = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    isTeacher.set(false);
    dialogMock.open.mockReset();
    scheduleServiceMock.getMySchedule.mockReset();

    await TestBed.configureTestingModule({
      imports: [ScheduleComponent],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
        {
          provide: MatDialog,
          useValue: dialogMock,
        },
        {
          provide: ScheduleService,
          useValue: scheduleServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ScheduleComponent);
    component = fixture.componentInstance;
  });
  it('should expose material action only for teachers', () => {
    expect(component['displayedColumns']()).not.toContain('materials');

    isTeacher.set(true);

    expect(component['displayedColumns']()).toContain('materials');
  });
  it('should load schedule for a valid date range', () => {
    const lessons: ScheduleLessonResponse[] = [
      {
        id: 1,
        name: 'Math lesson',
        date: '2023-04-27',
        startTime: '10:00:00',
        endTime: '12:00:00',
        subjectId: 1,
        subjectName: 'Mathematics',
        groupId: 1,
        groupName: 'Exact Sciences Group',
        teacherId: 1,
        teacherFirstName: 'Bob',
        teacherLastName: 'First',
      },
    ];

    scheduleServiceMock.getMySchedule.mockReturnValue(of(lessons));

    component['form'].setValue({
      from: '2023-04-25',
      to: '2023-04-29',
    });

    component['loadSchedule']();

    expect(scheduleServiceMock.getMySchedule).toHaveBeenCalledWith('2023-04-25', '2023-04-29');

    expect(component['lessons']()).toEqual(lessons);
    expect(component['loaded']()).toBe(true);
    expect(component['errorMessage']()).toBeNull();
  });

  it('should not call service when end date is before start date', () => {
    component['form'].setValue({
      from: '2023-04-29',
      to: '2023-04-25',
    });

    component['loadSchedule']();

    expect(scheduleServiceMock.getMySchedule).not.toHaveBeenCalled();
    expect(component['lessons']()).toEqual([]);
    expect(component['errorMessage']()).toBe('End date cannot be before start date.');
  });

  it('should show authorization error when backend returns 403', () => {
    scheduleServiceMock.getMySchedule.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    component['form'].setValue({
      from: '2023-04-25',
      to: '2023-04-29',
    });

    component['loadSchedule']();

    expect(component['lessons']()).toEqual([]);
    expect(component['loaded']()).toBe(true);
    expect(component['errorMessage']()).toBe('You are not allowed to view a personal schedule.');
  });

  it('should keep empty lessons when backend returns no schedule', () => {
    scheduleServiceMock.getMySchedule.mockReturnValue(of([]));

    component['form'].setValue({
      from: '2026-09-17',
      to: '2026-09-18',
    });

    component['loadSchedule']();

    expect(component['lessons']()).toEqual([]);
    expect(component['loaded']()).toBe(true);
    expect(component['errorMessage']()).toBeNull();
  });
});
