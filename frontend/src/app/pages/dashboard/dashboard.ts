import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { KeycloakService } from '../../core/auth/keycloak';
import { TaskService } from '../../core/services/task';
import { Task } from '../../models/task';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  protected readonly keycloakService = inject(KeycloakService);
  private readonly taskService = inject(TaskService);

  protected readonly tasks = signal<Task[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal('');

  protected readonly roles = signal<string[]>(this.keycloakService.getRoles());

  protected readonly isAdmin = computed(() => this.roles().includes('ADMIN'));

  protected readonly totalTasks = computed(() => this.tasks().length);

  protected readonly todoTasks = computed(
    () => this.tasks().filter((task) => task.status === 'TODO').length,
  );

  protected readonly inProgressTasks = computed(
    () => this.tasks().filter((task) => task.status === 'IN_PROGRESS').length,
  );

  protected readonly doneTasks = computed(
    () => this.tasks().filter((task) => task.status === 'DONE').length,
  );

  ngOnInit(): void {
    console.log('Current roles:', this.roles());

    this.loadTasks();
  }

  loadTasks(): void {
    this.loading.set(true);
    this.error.set('');

    this.taskService.getTasks().subscribe({
      next: (tasks) => {
        this.tasks.set(tasks);
        this.loading.set(false);
      },

      error: (error) => {
        console.error('Failed to load dashboard tasks:', error);

        this.error.set('Could not load task statistics.');
        this.loading.set(false);
      },
    });
  }

  async logout(): Promise<void> {
    await this.keycloakService.logout();
  }
}
