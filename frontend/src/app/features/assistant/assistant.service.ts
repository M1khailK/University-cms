import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AssistantMessageRequest, AssistantMessageResponse } from './assistant.models';

@Injectable({
  providedIn: 'root',
})
export class AssistantService {
  private readonly http = inject(HttpClient);

  sendMessage(request: AssistantMessageRequest): Observable<AssistantMessageResponse> {
    return this.http.post<AssistantMessageResponse>('/api/v1/assistant/messages', request);
  }
}
