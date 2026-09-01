import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TaskService } from '../../core/services/task';
import { Task } from '../../models/task';
import { HttpErrorResponse } from '@angular/common/http';

interface ValidationErrorResponse {
  status: number;
  message: string;
  errors?: Record<string, string>;
}

@Component({
  selector: 'app-tasks',
  imports: [CommonModule, FormsModule],
  templateUrl: './tasks.html',
  styleUrl: './tasks.scss',
})
export class Tasks {
  private readonly taskService = inject(TaskService);

  protected readonly tasks = signal<Task[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal('');

  // CREATE
  protected readonly creating = signal(false);
  protected readonly createError = signal('');
  protected readonly createSuccess = signal('');
  protected readonly deletingTaskId = signal<number | null>(null);
  protected readonly deleteError = signal('');
  protected readonly today = this.getToday();

  protected newTask = {
    title: '',
    description: '',
    priority: 'MEDIUM' as 'LOW' | 'MEDIUM' | 'HIGH',
    deadline: '',
  };

  // EDIT
  protected readonly editingTaskId = signal<number | null>(null);
  protected readonly updating = signal(false);
  protected readonly updateError = signal('');
  protected readonly updateSuccess = signal('');
  protected readonly updatingStatusTaskId = signal<number | null>(null);

  protected editTask = {
    title: '',
    description: '',
    priority: 'MEDIUM' as 'LOW' | 'MEDIUM' | 'HIGH',
    status: 'TODO' as 'TODO' | 'IN_PROGRESS' | 'DONE',
    deadline: '',
  };

  ngOnInit(): void {
    this.loadTasks();
  }

  private getToday(): string {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
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
        console.error('Failed to load tasks:', error);

        this.error.set('Could not load tasks.');
        this.loading.set(false);
      },
    });
  }

  createTask(): void {
    this.createError.set('');
    this.createSuccess.set('');

    const title = this.newTask.title.trim();
    const description = this.newTask.description.trim();

    if (!title) {
      this.createError.set('Title is required.');
      return;
    }

    if (title.length > 150) {
      this.createError.set('Title must contain at most 150 characters.');
      return;
    }

    if (!description) {
      this.createError.set('Description is required.');
      return;
    }

    if (description.length > 1000) {
      this.createError.set('Description must contain at most 1000 characters.');
      return;
    }

    if (!this.newTask.deadline) {
      this.createError.set('Deadline is required.');
      return;
    }

    if (this.newTask.deadline < this.today) {
      this.createError.set('Deadline cannot be in the past.');
      return;
    }

    this.creating.set(true);

    this.taskService
      .createTask({
        title,
        description,
        priority: this.newTask.priority,
        deadline: this.newTask.deadline,
      })
      .subscribe({
        next: () => {
          this.creating.set(false);
          this.createSuccess.set('Task created successfully.');

          this.newTask = {
            title: '',
            description: '',
            priority: 'MEDIUM',
            deadline: '',
          };

          this.loadTasks();
        },

        error: (error: HttpErrorResponse) => {
          console.error('Failed to create task:', error);

          this.createError.set(this.getErrorMessage(error, 'Could not create task.'));

          this.creating.set(false);
        },
      });
  }

  deleteTask(task: Task): void {
    const confirmed = window.confirm(`Are you sure you want to delete "${task.title}"?`);

    if (!confirmed) {
      return;
    }

    this.deleteError.set('');
    this.deletingTaskId.set(task.id);

    this.taskService.deleteTask(task.id).subscribe({
      next: () => {
        this.deletingTaskId.set(null);

        this.tasks.update((tasks) => tasks.filter((currentTask) => currentTask.id !== task.id));
      },

      error: (error) => {
        console.error('Failed to delete task:', error);

        this.deleteError.set('Could not delete task.');
        this.deletingTaskId.set(null);
      },
    });
  }

  changeStatus(task: Task): void {
    let nextStatus: 'TODO' | 'IN_PROGRESS' | 'DONE';

    if (task.status === 'TODO') {
      nextStatus = 'IN_PROGRESS';
    } else if (task.status === 'IN_PROGRESS') {
      nextStatus = 'DONE';
    } else {
      nextStatus = 'TODO';
    }

    this.updatingStatusTaskId.set(task.id);
    this.updateError.set('');

    this.taskService
      .updateTask(task.id, {
        title: task.title,
        description: task.description,
        priority: task.priority,
        status: nextStatus,
        deadline: task.deadline,
      })
      .subscribe({
        next: (updatedTask) => {
          this.tasks.update((tasks) =>
            tasks.map((currentTask) =>
              currentTask.id === updatedTask.id ? updatedTask : currentTask,
            ),
          );

          this.updatingStatusTaskId.set(null);
        },

        error: (error) => {
          console.error('Failed to update task status:', error);

          this.updateError.set('Could not update task status.');
          this.updatingStatusTaskId.set(null);
        },
      });
  }

  startEdit(task: Task): void {
    this.updateError.set('');
    this.updateSuccess.set('');

    this.editingTaskId.set(task.id);

    this.editTask = {
      title: task.title,
      description: task.description,
      priority: task.priority,
      status: task.status,
      deadline: task.deadline,
    };
  }

  private getErrorMessage(error: HttpErrorResponse, fallbackMessage: string): string {
    if (error.status === 400 && error.error) {
      const response = error.error as ValidationErrorResponse;

      if (response.errors) {
        const messages = Object.values(response.errors);

        if (messages.length > 0) {
          return messages.join(' ');
        }
      }

      if (response.message) {
        return response.message;
      }
    }

    return fallbackMessage;
  }

  cancelEdit(): void {
    this.editingTaskId.set(null);
    this.updateError.set('');
  }

  saveEdit(): void {
    const taskId = this.editingTaskId();

    if (taskId === null) {
      return;
    }

    this.updateError.set('');
    this.updateSuccess.set('');

    if (
      !this.editTask.title.trim() ||
      !this.editTask.description.trim() ||
      !this.editTask.deadline
    ) {
      this.updateError.set('Please complete all fields.');
      return;
    }

    this.updating.set(true);

    this.taskService
      .updateTask(taskId, {
        title: this.editTask.title,
        description: this.editTask.description,
        priority: this.editTask.priority,
        status: this.editTask.status,
        deadline: this.editTask.deadline,
      })
      .subscribe({
        next: () => {
          this.updating.set(false);
          this.editingTaskId.set(null);
          this.updateSuccess.set('Task updated successfully.');

          this.loadTasks();
        },

        error: (error: HttpErrorResponse) => {
          console.error('Failed to update task:', error);

          this.updateError.set(this.getErrorMessage(error, 'Could not update task.'));

          this.updating.set(false);
        },
      });
  }
}
