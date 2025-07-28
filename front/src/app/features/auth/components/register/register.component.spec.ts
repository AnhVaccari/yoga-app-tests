import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';
import { AuthService } from 'src/app/features/auth/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;

  const mockAuthService = {
    register: jest.fn(),
  };

  const mockRouter = {
    navigate: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
      ],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
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

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have invalid form when fields are empty', () => {
    expect(component.form.valid).toBeFalsy();
    expect(component.form.get('firstName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('lastName')?.hasError('required')).toBeTruthy();
    expect(component.form.get('email')?.hasError('required')).toBeTruthy();
    expect(component.form.get('password')?.hasError('required')).toBeTruthy();
  });

  it('should have invalid form when email format is wrong', () => {
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'invalid-email',
      password: 'password123',
    });

    expect(component.form.get('email')?.hasError('email')).toBeTruthy();
    expect(component.form.valid).toBeFalsy();
  });

  it('should register successfully', () => {
    mockAuthService.register.mockReturnValue(of(void 0));

    // Remplir le formulaire avec des données valides
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      password: 'password123',
    });

    component.submit();

    expect(mockAuthService.register).toHaveBeenCalledWith({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      password: 'password123',
    });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should display error on registration failure', () => {
    mockAuthService.register.mockReturnValue(throwError('Registration failed'));

    // Remplir le formulaire
    component.form.patchValue({
      firstName: 'John',
      lastName: 'Doe',
      email: 'john.doe@test.com',
      password: 'password123',
    });

    component.submit();

    expect(component.onError).toBeTruthy();

    // Vérifier que le message d'erreur s'affiche
    fixture.detectChanges();
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('An error occurred');
  });
});
