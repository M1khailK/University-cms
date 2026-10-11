import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { filter, finalize, switchMap, tap } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { GroupFormDialogComponent } from './form/group-form-dialog.component';
import { GroupRequest, GroupResponse } from './group.models';
import { GroupService } from './group.service';

@Component({
  selector: 'app-groups',
  imports: [RouterLink, MatButtonModule, MatDialogModule],
  templateUrl: './groups.component.html',
  styleUrl: './groups.component.scss',
})
export class GroupsComponent implements OnInit {
  private readonly groupService = inject(GroupService);
  private readonly authService = inject(AuthService);
  private readonly dialog = inject(MatDialog);

  protected readonly isAuthenticated = this.authService.isAuthenticated;
  protected readonly isAdmin = this.authService.isAdmin;

  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly query = signal('');
  protected readonly loading = signal(false);
  protected readonly saving = signal(false);
  protected readonly deletingGroupId = signal<number | null>(null);
  protected readonly confirmingDeleteId = signal<number | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly actionError = signal<string | null>(null);

  protected readonly filteredGroups = computed(() => {
    const normalizedQuery = this.query().trim().toLocaleLowerCase();

    if (!normalizedQuery) {
      return this.groups();
    }

    return this.groups().filter((group) =>
      group.name.toLocaleLowerCase().includes(normalizedQuery),
    );
  });

  ngOnInit(): void {
    this.loadGroups();
  }

  protected updateQuery(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.query.set(input.value);
  }

  protected loadGroups(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.groupService
      .getGroups()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (groups) => {
          this.groups.set(this.sortGroups(groups));
        },
        error: () => {
          this.errorMessage.set('The group catalog could not be loaded.');
        },
      });
  }

  protected openCreateDialog(): void {
    this.openFormDialog(null);
  }

  protected openEditDialog(group: GroupResponse): void {
    this.openFormDialog(group);
  }
  protected requestDelete(groupId: number): void {
    this.actionError.set(null);
    this.confirmingDeleteId.set(groupId);
  }

  protected cancelDelete(): void {
    this.confirmingDeleteId.set(null);
  }

  protected deleteGroup(group: GroupResponse): void {
    if (this.deletingGroupId() !== null) {
      return;
    }

    this.actionError.set(null);
    this.deletingGroupId.set(group.id);

    this.groupService
      .deleteGroup(group.id)
      .pipe(
        finalize(() => {
          this.deletingGroupId.set(null);
          this.confirmingDeleteId.set(null);
        }),
      )
      .subscribe({
        next: () => {
          this.groups.update((groups) => groups.filter((candidate) => candidate.id !== group.id));
        },
        error: (error: unknown) => {
          this.actionError.set(this.getActionErrorMessage(error, 'delete'));
        },
      });
  }
  private openFormDialog(group: GroupResponse | null): void {
    this.actionError.set(null);

    this.dialog
      .open(GroupFormDialogComponent, {
        data: { group },
        width: '30rem',
        maxWidth: 'calc(100vw - 2rem)',
        autoFocus: 'first-tabbable',
      })
      .afterClosed()
      .pipe(
        filter((request): request is GroupRequest => request !== undefined),
        tap(() => this.saving.set(true)),
        switchMap((request) => {
          const operation =
            group === null
              ? this.groupService.createGroup(request)
              : this.groupService.updateGroup(group.id, request);

          return operation.pipe(finalize(() => this.saving.set(false)));
        }),
      )
      .subscribe({
        next: (savedGroup) => {
          this.groups.update((groups) => {
            const remaining = groups.filter((candidate) => candidate.id !== savedGroup.id);

            return this.sortGroups([...remaining, savedGroup]);
          });
        },
        error: (error: unknown) => {
          this.actionError.set(
            this.getActionErrorMessage(error, group === null ? 'create' : 'update'),
          );
        },
      });
  }

  private sortGroups(groups: GroupResponse[]): GroupResponse[] {
    return [...groups].sort((left, right) => left.name.localeCompare(right.name));
  }

  private getActionErrorMessage(error: unknown, action: 'create' | 'update' | 'delete'): string {
    if (!(error instanceof HttpErrorResponse)) {
      return `Failed to ${action} group.`;
    }
    if (error.status === 403) {
      return 'Administrator permissions are required.';
    }
    if (error.status === 404) {
      return 'The group no longer exists.';
    }
    if (error.status === 409) {
      return 'The group cannot be deleted while it has assigned students.';
    }
    if (error.status === 400) {
      return 'Check the group name and try again.';
    }

    return `Failed to ${action} group.`;
  }
}
