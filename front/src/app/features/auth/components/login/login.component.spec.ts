import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  const mockAuthService = {
    login: jest.fn(),
  };

  const mockRouter = {
    navigate: jest.fn(),
  };

  const mockSessionService = {
    logIn: jest.fn(),
  };

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
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when fields are empty', () => {
    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('email')?.hasError('required')).toBeTruthy();
    expect(component.form.get('password')?.hasError('required')).toBeTruthy();
  });

  it('should toggle password visibility', () => {
    // État initial
    expect(component.hide).toBeTruthy();

    // Simule un clic sur le bouton toggle
    const compiled = fixture.nativeElement;
    const toggleButton = compiled.querySelector('button[mat-icon-button]');
    toggleButton.click();

    // Vérifie que hide a changé
    expect(component.hide).toBeFalsy();

    // Clic à nouveau
    toggleButton.click();
    expect(component.hide).toBeTruthy();
  });

  it('should login successfully', () => {
    // Mock de la réponse de connexion
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

    // Remplir le formulaire
    component.form.patchValue({
      email: 'test@test.com',
      password: 'password123',
    });

    component.submit();

    expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123',
    });
    expect(mockSessionService.logIn).toHaveBeenCalledWith(mockResponse);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });

  it('should display error on login failure', () => {
    // Mock d'une erreur
    mockAuthService.login.mockReturnValue(throwError('Login failed'));

    // Remplir le formulaire
    component.form.patchValue({
      email: 'wrong@test.com',
      password: 'wrongpassword',
    });

    component.submit();

    expect(component.onError).toBeTruthy();

    // Vérifier que le message d'erreur s'affiche
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('An error occurred');
  });

  it('should login and redirect (integration test)', () => {
    // Mock
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

    // Remplir le formulaire
    component.form.patchValue({
      email: 'test@test.com',
      password: 'password123',
    });

    component.submit();

    // Vérifications
    expect(mockAuthService.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: 'password123',
    });
    expect(mockSessionService.logIn).toHaveBeenCalledWith(mockResponse);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
  });
});
