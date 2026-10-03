import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { vi } from 'vitest';
import { SubjectFormDialogComponent } from './subject-form-dialog.component';

describe('SubjectFormDialogComponent', () => {
  const dialogRefMock = {
    close: vi.fn(),
  };

  beforeEach(async () => {
    dialogRefMock.close.mockReset();

    await TestBed.configureTestingModule({
      imports: [SubjectFormDialogComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            subject: null,
          },
        },
        {
          provide: MatDialogRef,
          useValue: dialogRefMock,
        },
      ],
    }).compileComponents();
  });

  it('should submit a trimmed subject name', () => {
    const component = TestBed.createComponent(SubjectFormDialogComponent).componentInstance;

    component['form'].controls.name.setValue('  Biology  ');

    component['submit']();

    expect(dialogRefMock.close).toHaveBeenCalledWith({
      name: 'Biology',
    });
  });

  it('should reject a whitespace-only subject name', () => {
    const component = TestBed.createComponent(SubjectFormDialogComponent).componentInstance;

    component['form'].controls.name.setValue('   ');

    component['submit']();

    expect(component['form'].controls.name.hasError('required')).toBe(true);

    expect(dialogRefMock.close).not.toHaveBeenCalled();
  });
});
