import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatPaginator } from '@angular/material/paginator';
import { By } from '@angular/platform-browser';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { StudentsPageResponse } from './student.models';
import { StudentService } from './student.service';
import { StudentsComponent } from './students.component';
import { provideRouter } from '@angular/router';
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
  };

  beforeEach(async () => {
    studentService.getStudents.mockReset();
    studentService.getStudents.mockReturnValue(of(firstPage));

    await TestBed.configureTestingModule({
      imports: [StudentsComponent],
      providers: [{ provide: StudentService, useValue: studentService }, provideRouter([])],
    }).compileComponents();

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
});
