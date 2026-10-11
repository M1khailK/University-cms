import { TestBed } from '@angular/core/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { vi } from 'vitest';
import { GroupFormDialogComponent } from './group-form-dialog.component';

describe('GroupFormDialogComponent', () => {
  const dialogRefMock = {
    close: vi.fn(),
  };

  beforeEach(async () => {
    dialogRefMock.close.mockReset();

    await TestBed.configureTestingModule({
      imports: [GroupFormDialogComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            group: null,
          },
        },
        {
          provide: MatDialogRef,
          useValue: dialogRefMock,
        },
      ],
    }).compileComponents();
  });

  it('should submit a trimmed group name', () => {
    const component = TestBed.createComponent(GroupFormDialogComponent).componentInstance;

    component['form'].controls.name.setValue('  Java-03  ');

    component['submit']();

    expect(dialogRefMock.close).toHaveBeenCalledWith({
      name: 'Java-03',
    });
  });

  it('should reject a whitespace-only group name', () => {
    const component = TestBed.createComponent(GroupFormDialogComponent).componentInstance;

    component['form'].controls.name.setValue('   ');

    component['submit']();

    expect(component['form'].controls.name.hasError('required')).toBe(true);
    expect(dialogRefMock.close).not.toHaveBeenCalled();
  });
});
