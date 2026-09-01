import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../../models/task';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = '/api/tasks';

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  getTasksByUser(userId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/user/${userId}`);
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: {
    title: string;
    description: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    deadline: string;
  }): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }
  updateTask(
    id: number,
    task: {
      title: string;
      description: string;
      priority: 'LOW' | 'MEDIUM' | 'HIGH';
      status: 'TODO' | 'IN_PROGRESS' | 'DONE';
      deadline: string;
    },
  ): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
