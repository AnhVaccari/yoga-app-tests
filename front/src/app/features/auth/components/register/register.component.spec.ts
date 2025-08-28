import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

describe('RegisterComponent (unit tests)', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  const mockAuthService = { register: jest.fn() };
  const mockRouter = { navigate: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
      imports: [
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => expect(component).toBeTruthy());

  it('should have invalid form when fields are empty', () => {
    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('firstName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('lastName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('email')?.hasError('required')).toBeTruthy();
    expect(component.form.get('password')?.hasError('required')).toBeTruthy();
  });

  it('should register successfully', () => {
    mockAuthService.register.mockReturnValue(of(void 0));
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      password: 'password123',
    });
    component.submit();
    expect(mockAuthService.register).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should display error on registration failure', () => {
    mockAuthService.register.mockReturnValue(throwError('Registration failed'));
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      password: 'password123',
    });
    component.submit();
    expect(component.onError).toBeTruthy();
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

  it('should register and redirect (integration test)', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    // Mock
    const mockResponse = {
      token: 'abcd',
      type: 'Bearer',
      id: 2,
      username: 'newuser@test.com',
      firstName: 'New',
      lastName: 'User',
      admin: false,
    };

    jest.spyOn(authService, 'register').mockReturnValue(of(void 0));

    // Remplir le formulaire
    component.form.patchValue({
      email: 'newuser@test.com',
      firstName: 'New',
      lastName: 'User',
      password: 'password123',
    });

    component.submit();

    expect(authService.register).toHaveBeenCalledWith({
      email: 'newuser@test.com',
      firstName: 'New',
      lastName: 'User',
      password: 'password123',
    });
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should handle register error (integration test)', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    jest
      .spyOn(authService, 'register')
      .mockReturnValue(throwError(() => 'Registration failed'));

    component.form.patchValue({
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      password: 'password123',
    });

    component.submit();

    expect(authService.register).toHaveBeenCalled();
    expect(component.onError).toBe(true);
    expect(navigateSpy).not.toHaveBeenCalled();
  });
});
