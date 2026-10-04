import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { AuthService } from '../auth/auth.service';
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

  const groupServiceMock = {
    getGroups: vi.fn(),
  };

  beforeEach(async () => {
    isAuthenticated.set(false);
    groupServiceMock.getGroups.mockReset();
    groupServiceMock.getGroups.mockReturnValue(of(initialGroups));

    await TestBed.configureTestingModule({
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
          },
        },
      ],
    }).compileComponents();
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
});

function inputEvent(value: string): Event {
  return {
    target: {
      value,
    },
  } as unknown as Event;
}
