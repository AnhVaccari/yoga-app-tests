import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent (unit tests)', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockAuthService = { login: jest.fn() };
  const mockRouter = { navigate: jest.fn() };
  const mockSessionService = { logIn: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService },
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    // @ts-ignore
    expect(component).toBeTruthy();
  });

  it('should have invalid form when fields are empty', () => {
    // @ts-ignore
    expect(component.form.valid).toBeFalsy();
    // @ts-ignore
    expect(component.form.get('email')?.hasError('required')).toBeTruthy();
    // @ts-ignore
    expect(component.form.get('password')?.hasError('required')).toBeTruthy();
  });

  it('should toggle password visibility', () => {
    // @ts-ignore
    expect(component.hide).toBeTruthy();
    const compiled = fixture.nativeElement;
    const toggleButton = compiled.querySelector('button[mat-icon-button]');
    toggleButton.click();

    fixture.detectChanges();
    // @ts-ignore
    expect(component.hide).toBeFalsy();
    toggleButton.click();
    fixture.detectChanges();

    // @ts-ignore
    expect(component.hide).toBeTruthy();
  });
});

describe('LoginComponent (integration tests)', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockAuthService = { login: jest.fn() };
  const mockRouter = { navigate: jest.fn() };
  const mockSessionService = { logIn: jest.fn() };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        { provide: SessionService, useValue: mockSessionService },
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should login successfully', () => {
    const mockResponse = {
      token: 'abcd',
      type: 'Bearer',
      id: 1,
      username: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };
    mockAuthService.login.mockReturnValue(of(mockResponse));

    component.form.patchValue({
      email: 'test@test.com',
      password: 'password123',
    });

    component.submit();

    // @ts-ignore
    expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123',
    });
    // @ts-ignore
    expect(mockSessionService.logIn).toHaveBeenCalledWith(mockResponse);
    // @ts-ignore
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should display error on login failure', () => {
    mockAuthService.login.mockReturnValue(throwError('Login failed'));

    component.form.patchValue({
      email: 'wrong@test.com',
      password: 'wrongpassword',
    });

    component.submit();

    // @ts-ignore
    expect(component.onError).toBeTruthy();
    fixture.detectChanges();
    const compiled = fixture.nativeElement;

    // @ts-ignore
    expect(compiled.textContent).toContain('An error occurred');
  });
});
