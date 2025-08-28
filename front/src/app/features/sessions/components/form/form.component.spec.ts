import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';

import { FormComponent } from './form.component';
import { SessionService } from '../../../../services/session.service';
import { SessionApiService } from '../../services/session-api.service';
import { TeacherService } from '../../../../services/teacher.service';

const mockSession = {
  id: 1,
  name: 'Test Session',
  date: '2025-08-01',
  teacher_id: 1,
  description: 'Test description',
  users: [1, 2],
  createdAt: new Date(),
  updatedAt: new Date(),
};

const mockTeachers = [
  { id: 1, firstName: 'John', lastName: 'Doe' },
  { id: 2, firstName: 'Jean', lastName: 'Dupont' },
];

describe('FormComponent (unit tests)', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = { sessionInformation: { admin: true } };
  const mockRouter = { navigate: jest.fn(), url: '/sessions/create' };
  const mockActivatedRoute = { snapshot: { paramMap: { get: jest.fn() } } };
  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession)),
    detail: jest.fn().mockReturnValue(of(mockSession)),
  };
  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of(mockTeachers)),
  };
  const mockMatSnackBar = { open: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      declarations: [FormComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate if user is not admin', () => {
    mockSessionService.sessionInformation.admin = false;
    component.ngOnInit();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should initialize form in create mode', () => {
    mockRouter.url = '/sessions/create';
    component.ngOnInit();
    expect(component.onUpdate).toBeFalsy();
    expect(component.sessionForm).toBeDefined();
  });

  it('should initialize form in update mode', () => {
    mockRouter.url = '/sessions/update/1';
    mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');
    mockSessionApiService.detail.mockReturnValue(of(mockSession));
    component.ngOnInit();
    expect(component.onUpdate).toBeTruthy();
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.sessionForm?.get('name')?.value).toBe(mockSession.name);
  });
});

describe('FormComponent (integration tests)', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  const mockSessionService = { sessionInformation: { admin: true } };
  const mockRouter = { navigate: jest.fn(), url: '/sessions/create' };
  const mockActivatedRoute = { snapshot: { paramMap: { get: jest.fn() } } };
  const mockSessionApiService = {
    create: jest.fn().mockReturnValue(of(mockSession)),
    update: jest.fn().mockReturnValue(of(mockSession)),
    detail: jest.fn().mockReturnValue(of(mockSession)),
  };
  const mockTeacherService = {
    all: jest.fn().mockReturnValue(of(mockTeachers)),
  };
  const mockMatSnackBar = { open: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        MatSelectModule,
        BrowserAnimationsModule,
      ],
      declarations: [FormComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should load teachers and initialize form', () => {
    component.ngOnInit();
    expect(mockTeacherService.all).toHaveBeenCalled();
    expect(component.sessionForm).toBeDefined();
  });

  it('should create session with snackbar and navigate', () => {
    mockRouter.url = '/sessions/create';
    component.ngOnInit();

    const formData = {
      name: 'Test Session',
      date: '2025-08-01',
      teacher_id: 1,
      description: 'Test description',
    };
    component.sessionForm?.patchValue(formData);

    component.submit();

    expect(mockSessionApiService.create).toHaveBeenCalledWith(formData);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session created !',
      'Close',
      { duration: 3000 }
    );
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should update session with snackbar and navigate', () => {
    mockRouter.url = '/sessions/update/1';
    mockActivatedRoute.snapshot.paramMap.get.mockReturnValue('1');
    component.ngOnInit();

    const formData = {
      name: 'Test Session',
      date: '2025-08-01',
      teacher_id: 1,
      description: 'Test description',
    };
    component.sessionForm?.patchValue(formData);
    component.submit();

    expect(mockSessionApiService.update).toHaveBeenCalledWith('1', formData);
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Session updated !',
      'Close',
      { duration: 3000 }
    );
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });
});
