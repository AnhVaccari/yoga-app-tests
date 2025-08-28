import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { SessionService } from 'src/app/services/session.service';
import { of } from 'rxjs';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

import { MeComponent } from './me.component';

describe('MeComponent ', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1,
    },

    logOut: jest.fn(),
  };

  const mockUser = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@test.com',
    admin: true,
  };

  const mockUserService = {
    getById: jest.fn().mockReturnValue(of(mockUser)),
    delete: jest.fn().mockReturnValue(of({})),
  };

  const mockRouter = {
    navigate: jest.fn(),
  };

  const mockMatSnackBar = {
    open: jest.fn(),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: UserService, useValue: mockUserService },
        { provide: Router, useValue: mockRouter },
        { provide: MatSnackBar, useValue: mockMatSnackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display user information', () => {
    const compiled = fixture.nativeElement;

    // Vérifie que les infos du mockUser s'affichent
    expect(compiled.textContent).toContain('John');
    expect(compiled.textContent).toContain('DOE');
    expect(compiled.textContent).toContain('john@test.com');
  });

  it('should call window.history.back when back( ) is called', () => {
    const spy = jest.spyOn(window.history, 'back');

    component.back();

    // Vérifie que window.history.back a été appelé
    expect(spy).toHaveBeenCalled();
  });

  it('should delete user account', () => {
    component.delete();

    // Vérifie que userService.delete a été appelé avec le bon ID
    expect(mockUserService.delete).toHaveBeenCalledWith('1');

    // Vérifie que le snackbar s'affiche
    expect(mockMatSnackBar.open).toHaveBeenCalledWith(
      'Your account has been deleted !',
      'Close',
      { duration: 3000 }
    );

    // Vérifie que l'utilisateur est déconnecté
    expect(mockSessionService.logOut).toHaveBeenCalled();
    // Vérifie la navigation vers la page d'accueil
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should load user data and display information (integration test)', () => {
    const mockUser = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@test.com',
    };
    mockUserService.getById.mockReturnValue(of(mockUser));

    component.ngOnInit();

    expect(mockUserService.getById).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  it('should delete user and show snackbar with navigation (integration test)', () => {
    mockUserService.delete.mockReturnValue(of({}));

    component.delete();

    expect(mockUserService.delete).toHaveBeenCalledWith('1');
    expect(mockMatSnackBar.open).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
