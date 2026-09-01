import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { KeycloakService } from '../auth/keycloak';

export const adminGuard: CanActivateFn = () => {
  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  const roles = keycloakService.getRoles();

  if (roles.includes('ADMIN')) {
    return true;
  }

  return router.parseUrl('/dashboard');
};
