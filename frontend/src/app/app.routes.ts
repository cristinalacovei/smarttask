import { Routes } from '@angular/router';

import { Dashboard } from './pages/dashboard/dashboard';
import { Tasks } from './pages/tasks/tasks';
import { AiPlan } from './pages/ai-plan/ai-plan';
import { Admin } from './pages/admin/admin';
import { adminGuard } from './core/guard/admin-guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'dashboard',
    component: Dashboard,
  },
  {
    path: 'tasks',
    component: Tasks,
  },
  {
    path: 'ai-plan',
    component: AiPlan,
  },
  {
    path: 'admin',
    component: Admin,
    canActivate: [adminGuard],
  },
];
