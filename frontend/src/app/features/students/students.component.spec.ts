import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatPaginator } from '@angular/material/paginator';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { StudentsPageResponse } from './student.models';
import { StudentService } from './student.service';
import { StudentsComponent } from './students.component';
import { provideRouter } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
describe('StudentsComponent', () => {
  let fixture: ComponentFixture<StudentsComponent>;

  const firstPage: StudentsPageResponse = {
    content: [
      {
        id: 1,
        firstName: 'Alice',
        lastName: 'First',
        email: 'alice@example.com',
        groupId: 10,
        groupName: 'Java-01',
      },
    ],
    page: 0,
    size: 20,
    totalElements: 25,
    totalPages: 2,
  };

  const secondPage: StudentsPageResponse = {
    content: [
      {
        id: 21,
        firstName: 'Bob',
        lastName: 'Second',
        email: 'bob@example.com',
        groupId: 20,
        groupName: 'Java-02',
      },
    ],
    page: 1,
    size: 20,
    totalElements: 25,
    totalPages: 2,
  };

  const studentService = {
    getStudents: vi.fn(),
    deactivateStudent: vi.fn(),
  };

  const dialog = {
    open: vi.fn(),
  };

  beforeEach(async () => {
    studentService.getStudents.mockReset();
    studentService.deactivateStudent.mockReset();
    dialog.open.mockReset();

    studentService.getStudents.mockReturnValue(of(firstPage));
    studentService.deactivateStudent.mockReturnValue(of(undefined));

    TestBed.configureTestingModule({
      imports: [StudentsComponent],
      providers: [
        {
          provide: StudentService,
          useValue: studentService,
        },
        {
          provide: MatDialog,
          useValue: dialog,
        },
        provideRouter([]),
      ],
    });

    TestBed.overrideProvider(MatDialog, {
      useValue: dialog,
    });

    await TestBed.compileComponents();

    fixture = TestBed.createComponent(StudentsComponent);
  });

  it('should load the first students page on initialization', () => {
    fixture.detectChanges();

    expect(studentService.getStudents).toHaveBeenCalledWith(0, 20);
  });

  it('should load requested page when paginator changes', () => {
    studentService.getStudents
      .mockReturnValueOnce(of(firstPage))
      .mockReturnValueOnce(of(secondPage));

    fixture.detectChanges();

    const paginator = fixture.debugElement.query(By.directive(MatPaginator))
      .componentInstance as MatPaginator;

    paginator.page.emit({
      pageIndex: 1,
      previousPageIndex: 0,
      pageSize: 20,
      length: 25,
    });

    fixture.detectChanges();

    expect(studentService.getStudents).toHaveBeenNthCalledWith(2, 1, 20);
  });

  it('should not deactivate student when confirmation is cancelled', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(false),
    });

    fixture.detectChanges();

    fixture.componentInstance['deactivateStudent'](firstPage.content[0]);

    expect(dialog.open).toHaveBeenCalledTimes(1);
    expect(studentService.deactivateStudent).not.toHaveBeenCalled();
    expect(studentService.getStudents).toHaveBeenCalledTimes(1);
  });

  it('should deactivate student and reload current page when confirmed', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    studentService.getStudents
      .mockReturnValueOnce(of(firstPage))
      .mockReturnValueOnce(of(firstPage));

    fixture.detectChanges();

    fixture.componentInstance['deactivateStudent'](firstPage.content[0]);

    expect(studentService.deactivateStudent).toHaveBeenCalledWith(1);

    expect(studentService.getStudents).toHaveBeenNthCalledWith(2, 0, 20);
  });

  it('should load previous page when last student on current page is deactivated', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    studentService.getStudents
      .mockReturnValueOnce(of(firstPage))
      .mockReturnValueOnce(of(secondPage))
      .mockReturnValueOnce(of(firstPage));

    fixture.detectChanges();

    const paginator = fixture.debugElement.query(By.directive(MatPaginator))
      .componentInstance as MatPaginator;

    paginator.page.emit({
      pageIndex: 1,
      previousPageIndex: 0,
      pageSize: 20,
      length: 25,
    });

    fixture.detectChanges();

    fixture.componentInstance['deactivateStudent'](secondPage.content[0]);

    expect(studentService.deactivateStudent).toHaveBeenCalledWith(21);

    expect(studentService.getStudents).toHaveBeenNthCalledWith(3, 0, 20);
  });

  it('should show error when student deactivation is forbidden', () => {
    dialog.open.mockReturnValue({
      afterClosed: () => of(true),
    });

    studentService.deactivateStudent.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 403,
          }),
      ),
    );

    fixture.detectChanges();

    fixture.componentInstance['deactivateStudent'](firstPage.content[0]);

    fixture.detectChanges();

    expect(fixture.componentInstance['actionError']()).toBe(
      'You are not allowed to deactivate students.',
    );

    const errorMessage = fixture.nativeElement.querySelector('.error-message') as HTMLElement;

    expect(errorMessage.textContent?.trim()).toBe('You are not allowed to deactivate students.');
  });
});
