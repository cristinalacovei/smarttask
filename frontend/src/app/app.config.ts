import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
} from "@angular/core";
import { provideRouter } from "@angular/router";
import { provideHttpClient, withInterceptors } from "@angular/common/http";

import { routes } from "./app.routes";
import { KeycloakService } from "./core/auth/keycloak";
import { authInterceptor } from "./core/interceptors/auth.interceptor";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),

    provideHttpClient(withInterceptors([authInterceptor])),

    provideAppInitializer(() => {
      const keycloakService = inject(KeycloakService);
      return keycloakService.init();
    }),
  ],
};
