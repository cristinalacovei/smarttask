import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { UserService } from '../../core/services/user';
import { TaskService } from '../../core/services/task';

import { User } from '../../models/user';
import { Task } from '../../models/task';

@Component({
  selector: 'app-admin',
  imports: [],
  templateUrl: './admin.html',
  styleUrl: './admin.scss',
})
export class Admin {
  private readonly userService = inject(UserService);
  private readonly taskService = inject(TaskService);

  protected readonly users = signal<User[]>([]);
  protected readonly loading = signal(false);
  protected readonly error = signal('');

  protected readonly selectedUser = signal<User | null>(null);
  protected readonly selectedUserTasks = signal<Task[]>([]);
  protected readonly loadingTasks = signal(false);
  protected readonly tasksError = signal('');

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading.set(true);
    this.error.set('');

    this.userService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },

      error: (error) => {
        console.error('Failed to load users:', error);

        if (error.status === 403) {
          this.error.set('You do not have permission to access this page.');
        } else {
          this.error.set('Could not load users.');
        }

        this.loading.set(false);
      },
    });
  }

  viewTasks(user: User): void {
    this.selectedUser.set(user);
    this.selectedUserTasks.set([]);
    this.tasksError.set('');
    this.loadingTasks.set(true);

    this.taskService.getTasksByUser(user.id).subscribe({
      next: (tasks) => {
        this.selectedUserTasks.set(tasks);
        this.loadingTasks.set(false);
      },

      error: (error) => {
        console.error('Failed to load user tasks:', error);

        this.tasksError.set("Could not load this user's tasks.");
        this.loadingTasks.set(false);
      },
    });
  }

  closeTasks(): void {
    this.selectedUser.set(null);
    this.selectedUserTasks.set([]);
    this.tasksError.set('');
  }
}
