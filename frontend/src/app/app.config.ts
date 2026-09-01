import { ApplicationConfig, inject, provideAppInitializer } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { routes } from './app.routes';
import { KeycloakService } from './core/auth/keycloak';
import { authInterceptor } from './core/interceptors/auth.interceptor';
import { UserService } from './core/services/user';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    provideHttpClient(withInterceptors([authInterceptor])),

    provideAppInitializer(async () => {
      const keycloakService = inject(KeycloakService);
      const userService = inject(UserService);

      await keycloakService.init();

      if (keycloakService.isAuthenticated()) {
        await firstValueFrom(userService.getOrCreateCurrentUser());
      }
    }),
  ],
};
