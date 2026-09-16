import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { finalize } from 'rxjs';
import { AssistantService } from './assistant.service';

@Component({
  selector: 'app-assistant',
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './assistant.component.html',
  styleUrl: './assistant.component.scss',
})
export class AssistantComponent {
  private readonly assistantService = inject(AssistantService);

  protected readonly messageControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.pattern(/\S/), Validators.maxLength(2000)],
  });
  protected readonly form = new FormGroup({
    message: this.messageControl,
  });
  protected readonly answer = signal<string | null>(null);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected sendMessage(): void {
    if (this.messageControl.invalid || this.loading()) {
      this.messageControl.markAsTouched();
      return;
    }

    const message = this.messageControl.value.trim();

    this.loading.set(true);
    this.answer.set(null);
    this.errorMessage.set(null);

    this.assistantService
      .sendMessage({ message })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (response) => {
          this.answer.set(response.answer);
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getErrorMessage(error));
        },
      });
  }

  private getErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'Failed to contact the assistant.';
    }

    if (error.status === 400) {
      return 'Please enter a valid message.';
    }

    if (error.status === 401) {
      return 'Your session is no longer valid. Please sign in again.';
    }

    if (error.status === 403) {
      return 'You are not allowed to use the assistant.';
    }

    if (error.status === 503) {
      return 'The assistant is temporarily unavailable. Please try again later.';
    }

    return 'Failed to contact the assistant.';
  }
}
