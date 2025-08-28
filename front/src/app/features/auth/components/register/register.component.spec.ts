import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('RegisterComponent (unit tests)', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = { register: jest.fn() };
    mockRouter = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when empty', () => {
    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('firstName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('lastName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('email')?.hasError('required')).toBeTruthy();
    expect(component.form.get('password')?.hasError('required')).toBeTruthy();
  });

  it('should not call register if form is invalid', () => {
    // Formulaire vide ou invalide
    component.form.patchValue({
      firstName: '',
      lastName: '',
      email: '',
      password: '',
    });

    const spy = jest.spyOn(component['authService'], 'register');

    component.submit();

    expect(spy).not.toHaveBeenCalled();
  });

  it('should register successfully', () => {
    const formData = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@test.com',
      password: 'password123',
    };
    mockAuthService.register.mockReturnValue(of(void 0));

    component.form.patchValue(formData);
    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith(formData);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('should handle registration error', () => {
    const formData = {
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@test.com',
      password: 'password123',
    };
    mockAuthService.register.mockReturnValue(throwError(() => 'error'));

    component.form.patchValue(formData);
    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith(formData);
    expect(component.onError).toBeTruthy();
    expect(mockRouter.navigate).not.toHaveBeenCalled();
  });
});

describe('RegisterComponent (integration tests)', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      imports: [
        ReactiveFormsModule,
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([]),
      ],
      providers: [AuthService],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should register and navigate on success', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const formData = {
      firstName: 'New',
      lastName: 'User',
      email: 'newuser@test.com',
      password: 'password123',
    };

    jest.spyOn(authService, 'register').mockReturnValue(of(void 0));

    component.form.patchValue(formData);
    component.submit();

    expect(authService.register).toHaveBeenCalledWith(formData);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  it('should set onError on registration failure', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const formData = {
      firstName: 'Test',
      lastName: 'User',
      email: 'test@test.com',
      password: 'password123',
    };

    jest
      .spyOn(authService, 'register')
      .mockReturnValue(throwError(() => 'fail'));

    component.form.patchValue(formData);
    component.submit();

    expect(authService.register).toHaveBeenCalledWith(formData);
    expect(component.onError).toBe(true);
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
