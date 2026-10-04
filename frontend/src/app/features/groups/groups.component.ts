import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { GroupResponse } from './group.models';
import { GroupService } from './group.service';

@Component({
  selector: 'app-groups',
  imports: [RouterLink, MatButtonModule],
  templateUrl: './groups.component.html',
  styleUrl: './groups.component.scss',
})
export class GroupsComponent implements OnInit {
  private readonly groupService = inject(GroupService);
  private readonly authService = inject(AuthService);

  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected readonly groups = signal<GroupResponse[]>([]);
  protected readonly query = signal('');
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

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

  private sortGroups(groups: GroupResponse[]): GroupResponse[] {
    return [...groups].sort((left, right) => left.name.localeCompare(right.name));
  }
}
