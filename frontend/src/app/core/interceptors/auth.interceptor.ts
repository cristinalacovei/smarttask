import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { from } from "rxjs";
import { switchMap } from "rxjs/operators";
import { KeycloakService } from "../auth/keycloak";

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const keycloakService = inject(KeycloakService);

  return from(keycloakService.updateToken()).pipe(
    switchMap(() => {
      const token = keycloakService.getToken();

      if (!token) {
        return next(req);
      }

      const authenticatedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });

      return next(authenticatedRequest);
    }),
  );
};
