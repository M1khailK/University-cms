import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';

import { ProfileService } from '../profile.service';
import { ChangePasswordComponent } from './change-password.component';

describe('ChangePasswordComponent', () => {
  let fixture: ComponentFixture<ChangePasswordComponent>;
  let component: ChangePasswordComponent;

  let profileService: {
    changePassword: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    profileService = {
      changePassword: vi.fn().mockReturnValue(of(undefined)),
    };

    TestBed.configureTestingModule({
      imports: [ChangePasswordComponent],
      providers: [
        {
          provide: ProfileService,
          useValue: profileService,
        },
      ],
    });

    await TestBed.compileComponents();

    fixture = TestBed.createComponent(ChangePasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not submit an invalid form', () => {
    submitForm();

    expect(profileService.changePassword).not.toHaveBeenCalled();

    expect(
      fixture.nativeElement.querySelectorAll('.mat-form-field-invalid').length,
    ).toBeGreaterThan(0);
  });

  it('should reject mismatched new passwords', () => {
    component['form'].setValue({
      oldPassword: 'old-password',
      newPassword: 'new-password',
      confirmPassword: 'different-password',
    });

    submitForm();

    expect(profileService.changePassword).not.toHaveBeenCalled();

    expect(fixture.nativeElement.textContent).toContain('New passwords do not match.');
  });

  it('should change password and reset submitted state', () => {
    component['form'].setValue({
      oldPassword: 'old-password',
      newPassword: 'new-password',
      confirmPassword: 'new-password',
    });

    submitForm();

    expect(profileService.changePassword).toHaveBeenCalledWith({
      oldPassword: 'old-password',
      newPassword: 'new-password',
    });

    expect(component['form'].controls.oldPassword.value).toBe('');
    expect(component['form'].controls.newPassword.value).toBe('');
    expect(component['form'].controls.confirmPassword.value).toBe('');

    expect(fixture.nativeElement.textContent).toContain('Password changed successfully.');

    expect(fixture.nativeElement.querySelectorAll('.mat-form-field-invalid')).toHaveLength(0);
  });

  it('should show only incorrect current password error', () => {
    profileService.changePassword.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 400,
            error: {
              title: 'Invalid old password',
            },
          }),
      ),
    );

    component['form'].setValue({
      oldPassword: 'wrong-password',
      newPassword: 'new-password',
      confirmPassword: 'new-password',
    });

    submitForm();

    expect(fixture.nativeElement.textContent).toContain('The current password is incorrect.');

    expect(component['form'].controls.oldPassword.value).toBe('');
    expect(component['form'].controls.newPassword.value).toBe('new-password');

    expect(fixture.nativeElement.querySelectorAll('.mat-form-field-invalid')).toHaveLength(0);
  });

  it('should show generic request error', () => {
    profileService.changePassword.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 500,
          }),
      ),
    );

    component['form'].setValue({
      oldPassword: 'old-password',
      newPassword: 'new-password',
      confirmPassword: 'new-password',
    });

    submitForm();

    expect(fixture.nativeElement.textContent).toContain('Failed to change password.');
  });

  it('should disable submission while request is pending', () => {
    profileService.changePassword.mockReturnValue(new Subject<void>());

    component['form'].setValue({
      oldPassword: 'old-password',
      newPassword: 'new-password',
      confirmPassword: 'new-password',
    });

    submitForm();

    const button = fixture.nativeElement.querySelector(
      'button[type="submit"]',
    ) as HTMLButtonElement;

    expect(button.disabled).toBe(true);
    expect(button.textContent).toContain('Saving...');
  });

  function submitForm(): void {
    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;

    form.dispatchEvent(
      new Event('submit', {
        bubbles: true,
        cancelable: true,
      }),
    );

    fixture.detectChanges();
  }
});
