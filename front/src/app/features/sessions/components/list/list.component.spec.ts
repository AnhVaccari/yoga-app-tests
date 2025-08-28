import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { of } from 'rxjs';

import { ListComponent } from './list.component';
import { SessionApiService } from '../../services/session-api.service';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
    },
  };

  const mockSessions = [
    { id: 1, name: 'Session 1', description: 'Desc 1' },
    { id: 2, name: 'Session 2', description: 'Desc 2' },
  ];

  const mockSessionApiService = {
    all: jest.fn().mockReturnValue(of(mockSessions)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: SessionApiService, useValue: mockSessionApiService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load sessions from the service (integration test)', (done) => {
    component.sessions$.subscribe((sessions) => {
      expect(sessions).toEqual(mockSessions);
      done();
    });

    expect(mockSessionApiService.all).toHaveBeenCalled();
  });

  it('should expose user information from sessionService', () => {
    expect(component.user).toEqual(mockSessionService.sessionInformation);
  });
});
