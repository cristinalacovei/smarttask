import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { KeycloakService } from './core/auth/keycloak';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly keycloakService = inject(KeycloakService);

  protected readonly isAdmin = signal(this.keycloakService.getRoles().includes('ADMIN'));

  protected get username(): string {
    return this.keycloakService.getUsername() ?? 'User';
  }

  async logout(): Promise<void> {
    await this.keycloakService.logout();
  }
}
