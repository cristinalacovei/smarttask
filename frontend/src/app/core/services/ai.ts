import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AiService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/ai';

  generateStudyPlan(): Observable<string> {
    return this.http.post(`${this.apiUrl}/study-plan`, null, {
      responseType: 'text',
    });
  }
}
