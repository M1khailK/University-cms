import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { finalize, forkJoin } from 'rxjs';
import { GroupResponse, GroupStudentResponse } from '../group.models';
import { GroupService } from '../group.service';

@Component({
  selector: 'app-group-students',
  imports: [RouterLink, MatButtonModule],
  templateUrl: './group-students.component.html',
  styleUrl: './group-students.component.scss',
})
export class GroupStudentsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly groupService = inject(GroupService);

  protected readonly group = signal<GroupResponse | null>(null);
  protected readonly students = signal<GroupStudentResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    const rawGroupId = this.route.snapshot.paramMap.get('id');
    const groupId = Number(rawGroupId);

    if (rawGroupId === null || !Number.isInteger(groupId) || groupId <= 0) {
      this.errorMessage.set('The group address is invalid.');
      return;
    }

    this.loadGroupStudents(groupId);
  }

  private loadGroupStudents(groupId: number): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    forkJoin({
      group: this.groupService.getGroup(groupId),
      students: this.groupService.getGroupStudents(groupId),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: ({ group, students }) => {
          this.group.set(group);
          this.students.set(this.sortStudents(students));
        },
        error: (error: unknown) => {
          this.errorMessage.set(this.getLoadErrorMessage(error));
        },
      });
  }

  private sortStudents(students: GroupStudentResponse[]): GroupStudentResponse[] {
    return [...students].sort(
      (left, right) =>
        left.lastName.localeCompare(right.lastName) ||
        left.firstName.localeCompare(right.firstName) ||
        left.email.localeCompare(right.email),
    );
  }

  private getLoadErrorMessage(error: unknown): string {
    if (!(error instanceof HttpErrorResponse)) {
      return 'The group students could not be loaded.';
    }

    if (error.status === 403) {
      return 'Administrator permissions are required to view group students.';
    }

    if (error.status === 404) {
      return 'The group could not be found.';
    }

    return 'The group students could not be loaded.';
  }
}
