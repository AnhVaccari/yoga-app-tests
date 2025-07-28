import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { of } from 'rxjs';

import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
    },
  };

  const mockTeachers = [
    { id: 1, firstName: 'John', lastName: 'Doe' },
    { id: 2, firstName: 'Jean', lastName: 'Dupont' },
  ];

  const mockSession = {
    id: 1,
    name: 'Test Session',
    date: '2025-07-25',
    teacher_id: 1,
    description: 'Test description',
  };

  const mockRouter = {
    navigate: jest.fn(),
    url: '/sessions/create',
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1'),
      },
    },
  };

  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession)),
    detail: jest.fn().mockReturnValue(of(mockSession)),
  };

  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of(mockTeachers)),
  };

  const mockMatSnackBar = {
    open: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
      ],
      declarations: [FormComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to sessions if user is not admin', () => {
    //  Mock utilisateur non-admin
    mockSessionService.sessionInformation.admin = false;

    component.ngOnInit();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should initialize form in create mode', () => {
    mockRouter.url = '/sessions/create';

    component.ngOnInit();

    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.get('name')?.value).toBe('');
  });

  it('should initialize form in update mode', () => {
    mockRouter.url = '/sessions/update/1';

    component.ngOnInit();

    expect(component.onUpdate).toBeTruthy();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm?.get('name')?.value).toBe('Test Session');
  });

  it('should create session when submitting in create mode', () => {
    mockRouter.url = '/sessions/create';
    component.ngOnInit();
    component.onUpdate = false;

    const sessionData = {
      name: 'New Session',
      date: '2025-07-28',
      teacher_id: 1,
      description: 'New description',
    };
    component.sessionForm?.patchValue(sessionData);

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledWith(sessionData);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session created !',
      'Close',
      { duration: 3000 }
    );
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session when submitting in update mode', () => {
    mockRouter.url = '/sessions/update/1';
    component.ngOnInit();

    const updatedSessionData = {
      name: 'Updated Session',
      date: '2025-07-25',
      teacher_id: 2,
      description: 'Updated description',
    };
    component.sessionForm?.patchValue(updatedSessionData);

    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith(
      '1',
      updatedSessionData
    );
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session updated !',
      'Close',
      { duration: 3000 }
    );
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
