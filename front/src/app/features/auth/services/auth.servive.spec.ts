import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { LoginRequest } from '../interfaces/loginRequest.interface';
import { RegisterRequest } from '../interfaces/registerRequest.interface';
import { SessionInformation } from 'src/app/interfaces/sessionInformation.interface';
import { SessionService } from 'src/app/services/session.service';

describe('AuthService (unit tests)', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    // @ts-ignore
    expect(service).toBeTruthy();
  });
});

describe('AuthService (integration tests)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call login and return session information', () => {
    const loginRequest: LoginRequest = {
      email: 'test@test.com',
      password: 'password123',
    };

    const mockResponse: SessionInformation = {
      token: 'abcd',
      type: 'Bearer',
      id: 1,
      username: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };

    service.login(loginRequest).subscribe((response) => {
      // @ts-ignore
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/auth/login');
    // @ts-ignore
    expect(req.request.method).toBe('POST');
    // @ts-ignore
    expect(req.request.body).toEqual(loginRequest);
    req.flush(mockResponse);
  });

  it('should call register and succeed', () => {
    const registerRequest: RegisterRequest = {
      email: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123',
    };

    service.register(registerRequest).subscribe((response) => {
      // @ts-ignore
      expect(response).toBeUndefined();
    });

    const req = httpMock.expectOne('api/auth/register');
    // @ts-ignore
    expect(req.request.method).toBe('POST');
    // @ts-ignore
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });
});
