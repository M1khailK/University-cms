import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SubjectResponse } from './subject.models';
import { SubjectService } from './subject.service';

describe('SubjectService', () => {
  let service: SubjectService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SubjectService, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(SubjectService);

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should load all subjects', () => {
    const subjects: SubjectResponse[] = [
      { id: 1, name: 'Mathematics' },
      { id: 2, name: 'Software Architecture' },
    ];

    service.getSubjects().subscribe((response) => {
      expect(response).toEqual(subjects);
    });

    const request = httpTestingController.expectOne('/api/v1/subjects');

    expect(request.request.method).toBe('GET');

    request.flush(subjects);
  });

  it('should load a subject by id', () => {
    const subject: SubjectResponse = {
      id: 1,
      name: 'Mathematics',
    };

    service.getSubject(1).subscribe((response) => {
      expect(response).toEqual(subject);
    });

    const request = httpTestingController.expectOne('/api/v1/subjects/1');

    expect(request.request.method).toBe('GET');

    request.flush(subject);
  });

  it('should create a subject', () => {
    service.createSubject({ name: 'Biology' }).subscribe((response) => {
      expect(response).toEqual({
        id: 3,
        name: 'Biology',
      });
    });

    const request = httpTestingController.expectOne('/api/v1/subjects');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      name: 'Biology',
    });

    request.flush({
      id: 3,
      name: 'Biology',
    });
  });

  it('should update a subject', () => {
    service
      .updateSubject(3, {
        name: 'Advanced Biology',
      })
      .subscribe((response) => {
        expect(response.name).toBe('Advanced Biology');
      });

    const request = httpTestingController.expectOne('/api/v1/subjects/3');

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({
      name: 'Advanced Biology',
    });

    request.flush({
      id: 3,
      name: 'Advanced Biology',
    });
  });

  it('should delete a subject', () => {
    service.deleteSubject(3).subscribe();

    const request = httpTestingController.expectOne('/api/v1/subjects/3');

    expect(request.request.method).toBe('DELETE');

    request.flush(null, {
      status: 204,
      statusText: 'No Content',
    });
  });
});
