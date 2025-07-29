import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionService } from './session.service';
import { SessionInformation } from '../interfaces/sessionInformation.interface';

describe('SessionService', () => {
  let service: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return observable of logged state', () => {
    service.$isLogged().subscribe((isLogged) => {
      expect(isLogged).toBeFalsy();
    });
  });

  it('should log in user', () => {
    const mockUser: SessionInformation = {
      token: 'abcd',
      type: 'Bearer',
      id: 1,
      username: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };

    service.logIn(mockUser);

    expect(service.isLogged).toBeTruthy();
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('should log out user', () => {
    const mockUser: SessionInformation = {
      token: 'abcd',
      type: 'Bearer',
      id: 1,
      username: 'test@test.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
    };

    service.logIn(mockUser);

    service.logOut();

    expect(service.isLogged).toBeFalsy();
    expect(service.sessionInformation).toBeUndefined();
  });
});
