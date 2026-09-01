import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AiService } from '../../core/services/ai';

@Component({
  selector: 'app-ai-plan',
  imports: [],
  templateUrl: './ai-plan.html',
  styleUrl: './ai-plan.scss',
})
export class AiPlan {
  private readonly aiService = inject(AiService);

  protected readonly plan = signal('');
  protected readonly loading = signal(false);
  protected readonly error = signal('');

  generatePlan(): void {
    this.loading.set(true);
    this.error.set('');
    this.plan.set('');

    this.aiService.generateStudyPlan().subscribe({
      next: (plan) => {
        this.plan.set(plan);
        this.loading.set(false);
      },

      error: (error) => {
        console.error('Failed to generate AI plan:', error);

        this.error.set('Could not generate the study plan.');
        this.loading.set(false);
      },
    });
  }
}
