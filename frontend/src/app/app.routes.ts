import { Routes } from '@angular/router';
import { authGuard } from './features/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((module) => module.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then(
        (module) => module.DashboardComponent,
      ),
  },
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full',
  },
  {
    path: 'students',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/students/students.component').then((module) => module.StudentsComponent),
  },
  {
    path: 'students/new',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/students/create/student-create.component').then(
        (module) => module.StudentCreateComponent,
      ),
  },
  {
    path: 'students/:id/edit',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/students/edit/student-edit.component').then(
        (module) => module.StudentEditComponent,
      ),
  },
  {
    path: 'assistant',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/assistant/assistant.component').then(
        (module) => module.AssistantComponent,
      ),
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
