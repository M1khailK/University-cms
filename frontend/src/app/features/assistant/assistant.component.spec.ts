import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';
import { vi } from 'vitest';
import { AssistantComponent } from './assistant.component';
import { AssistantMessageResponse } from './assistant.models';
import { AssistantService } from './assistant.service';

describe('AssistantComponent', () => {
  let fixture: ComponentFixture<AssistantComponent>;
  let component: AssistantComponent;

  const assistantService = {
    sendMessage: vi.fn(),
  };

  beforeEach(async () => {
    assistantService.sendMessage.mockReset();
    assistantService.sendMessage.mockReturnValue(
      of({
        answer: 'Assistant response',
      }),
    );

    await TestBed.configureTestingModule({
      imports: [AssistantComponent],
      providers: [{ provide: AssistantService, useValue: assistantService }],
    }).compileComponents();

    fixture = TestBed.createComponent(AssistantComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should not send an invalid message', () => {
    component['messageControl'].setValue('   ');

    component['sendMessage']();

    expect(assistantService.sendMessage).not.toHaveBeenCalled();
  });

  it('should trim and send message and display assistant response', () => {
    component['messageControl'].setValue('  What can you help me with?  ');

    component['sendMessage']();

    expect(assistantService.sendMessage).toHaveBeenCalledWith({
      message: 'What can you help me with?',
    });

    expect(component['answer']()).toBe('Assistant response');
    expect(component['errorMessage']()).toBeNull();
    expect(component['loading']()).toBe(false);
  });

  it('should show unavailable message when assistant returns 503', () => {
    assistantService.sendMessage.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 503,
          }),
      ),
    );

    component['messageControl'].setValue('Hello');

    component['sendMessage']();

    expect(component['answer']()).toBeNull();
    expect(component['errorMessage']()).toBe(
      'The assistant is temporarily unavailable. Please try again later.',
    );
    expect(component['loading']()).toBe(false);
  });

  it('should not send another message while request is in progress', () => {
    const response$ = new Subject<AssistantMessageResponse>();
    assistantService.sendMessage.mockReturnValue(response$);

    component['messageControl'].setValue('Hello');

    component['sendMessage']();
    component['sendMessage']();

    expect(assistantService.sendMessage).toHaveBeenCalledOnce();
    expect(component['loading']()).toBe(true);

    response$.next({
      answer: 'Hello!',
    });
    response$.complete();

    expect(component['answer']()).toBe('Hello!');
    expect(component['loading']()).toBe(false);
  });

  it('should submit message through the form', () => {
  component['messageControl'].setValue('  Hello assistant  ');
  fixture.detectChanges();

  const form: HTMLFormElement = fixture.nativeElement.querySelector('form');

  form.dispatchEvent(
    new Event('submit', {
      bubbles: true,
      cancelable: true,
    }),
  );

  expect(assistantService.sendMessage).toHaveBeenCalledWith({
    message: 'Hello assistant',
  });
});
});
