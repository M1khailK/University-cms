import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { AuthService } from '../auth/auth.service';
import { GroupFormDialogComponent } from './form/group-form-dialog.component';
import { GroupResponse } from './group.models';
import { GroupService } from './group.service';
import { GroupsComponent } from './groups.component';

describe('GroupsComponent', () => {
  const initialGroups: GroupResponse[] = [
    {
      id: 2,
      name: 'Java-02',
    },
    {
      id: 1,
      name: 'Java-01',
    },
  ];

  const isAuthenticated = signal(false);
  const isAdmin = signal(false);

  const groupServiceMock = {
    getGroups: vi.fn(),
    createGroup: vi.fn(),
    updateGroup: vi.fn(),
    deleteGroup: vi.fn(),
  };

  const dialogMock = {
    open: vi.fn(),
  };
  beforeEach(async () => {
    isAuthenticated.set(false);
    isAdmin.set(false);

    groupServiceMock.getGroups.mockReset();
    groupServiceMock.createGroup.mockReset();
    groupServiceMock.updateGroup.mockReset();
    groupServiceMock.deleteGroup.mockReset();
    dialogMock.open.mockReset();

    groupServiceMock.getGroups.mockReturnValue(of(initialGroups));
    dialogMock.open.mockReturnValue({
      afterClosed: () => of(undefined),
    });

    TestBed.configureTestingModule({
      imports: [GroupsComponent],
      providers: [
        provideRouter([]),
        {
          provide: GroupService,
          useValue: groupServiceMock,
        },
        {
          provide: AuthService,
          useValue: {
            isAuthenticated,
            isAdmin,
          },
        },
      ],
    });

    TestBed.overrideProvider(MatDialog, {
      useValue: dialogMock,
    });

    await TestBed.compileComponents();
  });
  it('should load, sort and filter public groups', () => {
    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();

    expect(fixture.componentInstance['groups']().map((group) => group.name)).toEqual([
      'Java-01',
      'Java-02',
    ]);

    fixture.componentInstance['updateQuery'](inputEvent('02'));

    expect(fixture.componentInstance['filteredGroups']()).toEqual([
      {
        id: 2,
        name: 'Java-02',
      },
    ]);
  });

  it('should show a sign-in action to guests', () => {
    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('University groups');
    expect(content).toContain('Sign in');
    expect(content).not.toContain('Add group');
    expect(content).not.toContain('Edit');
    expect(content).not.toContain('Students');
  });

  it('should show an error state when groups cannot be loaded', () => {
    groupServiceMock.getGroups.mockReturnValue(throwError(() => new Error('Backend unavailable')));

    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(content).toContain('Catalog unavailable');
    expect(content).toContain('The group catalog could not be loaded.');
    expect(content).toContain('Try again');
  });

  it('should create a group from the administrator dialog result', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    const request = {
      name: 'Java-03',
    };

    const createdGroup: GroupResponse = {
      id: 3,
      name: 'Java-03',
    };

    dialogMock.open.mockReturnValue({
      afterClosed: () => of(request),
    });
    groupServiceMock.createGroup.mockReturnValue(of(createdGroup));

    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();
    fixture.componentInstance['openCreateDialog']();

    expect(dialogMock.open).toHaveBeenCalledWith(
      GroupFormDialogComponent,
      expect.objectContaining({
        data: {
          group: null,
        },
      }),
    );
    expect(groupServiceMock.createGroup).toHaveBeenCalledWith(request);
    expect(fixture.componentInstance['groups']()).toEqual([
      {
        id: 1,
        name: 'Java-01',
      },
      {
        id: 2,
        name: 'Java-02',
      },
      createdGroup,
    ]);
    expect(fixture.componentInstance['saving']()).toBe(false);
  });

  it('should update a group from the administrator dialog result', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    const groupToEdit: GroupResponse = {
      id: 1,
      name: 'Java-01',
    };

    const request = {
      name: 'Advanced Java',
    };

    const updatedGroup: GroupResponse = {
      id: 1,
      name: 'Advanced Java',
    };

    dialogMock.open.mockReturnValue({
      afterClosed: () => of(request),
    });
    groupServiceMock.updateGroup.mockReturnValue(of(updatedGroup));

    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();
    fixture.componentInstance['openEditDialog'](groupToEdit);

    expect(dialogMock.open).toHaveBeenCalledWith(
      GroupFormDialogComponent,
      expect.objectContaining({
        data: {
          group: groupToEdit,
        },
      }),
    );
    expect(groupServiceMock.updateGroup).toHaveBeenCalledWith(1, request);
    expect(fixture.componentInstance['groups']()).toEqual([
      updatedGroup,
      {
        id: 2,
        name: 'Java-02',
      },
    ]);
    expect(fixture.componentInstance['saving']()).toBe(false);
  });
  it('should delete a confirmed group', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    groupServiceMock.deleteGroup.mockReturnValue(of(undefined));

    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();

    const group = fixture.componentInstance['groups']()[0];

    fixture.componentInstance['requestDelete'](group.id);
    fixture.detectChanges();

    const confirmationContent = (fixture.nativeElement as HTMLElement).textContent ?? '';

    expect(confirmationContent).toContain('Delete this group?');
    expect(fixture.componentInstance['confirmingDeleteId']()).toBe(group.id);

    fixture.componentInstance['deleteGroup'](group);

    expect(groupServiceMock.deleteGroup).toHaveBeenCalledWith(group.id);
    expect(fixture.componentInstance['groups']()).not.toContainEqual(group);
    expect(fixture.componentInstance['deletingGroupId']()).toBeNull();
    expect(fixture.componentInstance['confirmingDeleteId']()).toBeNull();
  });

  it('should cancel group deletion without calling the service', () => {
    isAuthenticated.set(true);
    isAdmin.set(true);

    const fixture = TestBed.createComponent(GroupsComponent);

    fixture.detectChanges();

    const group = fixture.componentInstance['groups']()[0];

    fixture.componentInstance['requestDelete'](group.id);

    expect(fixture.componentInstance['confirmingDeleteId']()).toBe(group.id);

    fixture.componentInstance['cancelDelete']();

    expect(fixture.componentInstance['confirmingDeleteId']()).toBeNull();
    expect(groupServiceMock.deleteGroup).not.toHaveBeenCalled();
    expect(fixture.componentInstance['groups']()).toContainEqual(group);
  });
});

function inputEvent(value: string): Event {
  return {
    target: {
      value,
    },
  } as unknown as Event;
}
