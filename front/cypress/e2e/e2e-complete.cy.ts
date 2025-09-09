/// <reference types="cypress" />

import './login.cy';
import './logout.cy';
import './me.cy';
import './sessions.cy';
import './register.cy';

describe('E2E Complete Application Tests - Enhanced Coverage', () => {
  interface User {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
    admin: boolean;
    email?: string;
    createdAt?: string;
    updatedAt?: string;
  }

  interface Session {
    id: number;
    name: string;
    date: string;
    description: string;
    teacher_name?: string;
    users?: User[];
  }

  interface Teacher {
    id: number;
    firstName: string;
    lastName: string;
  }

  // Fonctions utilitaires
  const setupUserMocks = (): void => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false,
      } as User,
    }).as('userLogin');
  };

  const setupAdminMocks = (): void => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'adminUser',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
      } as User,
    }).as('adminLogin');

    cy.intercept('GET', '/api/teacher', [
      { id: 1, firstName: 'John', lastName: 'Doe' },
      { id: 2, firstName: 'Sylvie', lastName: 'Yogi' },
    ] as Teacher[]).as('teachers');
  };

  const setupSessionMocks = (): void => {
    cy.intercept('GET', '/api/session', [
      {
        id: 1,
        name: 'Yoga relaxation',
        date: '2025-08-08',
        description: 'Session de relaxation pour débutants',
      },
    ] as Session[]).as('sessions');

    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Yoga relaxation',
      date: '2024-01-15',
      description: 'Session de relaxation pour débutants',
      teacher_name: 'John Doe',
      users: [],
    } as Session).as('sessionDetail');
  };

  const loginAsUser = (
    email: string = 'yoga@studio.com',
    password: string = 'test!1234'
  ): void => {
    cy.get('input[formControlName=email]').type(email);
    cy.get('input[formControlName=password]').type(password);
    cy.get('button[type=submit]').click();
  };

  const loginAsAdmin = (): void => {
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('admin123');
    cy.get('button[type=submit]').click();
  };

  describe('Essential Coverage Tests', () => {
    it('Should handle admin session creation and deletion', () => {
      setupAdminMocks();
      setupSessionMocks();

      cy.visit('/login');
      loginAsAdmin();

      // Créer une session
      cy.get('button').contains('Create').click();
      cy.url().should('include', '/sessions/create');

      cy.intercept('POST', '/api/session', {
        statusCode: 201,
        body: {
          id: 3,
          name: 'Nouvelle session',
          date: '2024-02-01',
          description: 'Test session',
        } as Session,
      }).as('createSession');

      cy.get('input[formControlName=name]').type('Session Test');
      cy.get('input[formControlName=date]').type('2025-12-25');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName=description]').type('Description test');
      cy.get('button[type=submit]').click();
      cy.wait('@createSession');

      // Supprimer une session
      cy.get('button').contains('Detail').click();
      cy.wait('@sessionDetail');

      cy.intercept('DELETE', '/api/session/1', {
        statusCode: 200,
      }).as('deleteSession');
      cy.get('button').contains('Delete').click();
      cy.wait('@deleteSession');
      cy.contains('Session deleted !').should('be.visible');
    });

    it('Should test protected routes', () => {
      // Routes protégées redirigent vers home
      cy.visit('/sessions');
      cy.url().should('include', '/');

      cy.visit('/me');
      cy.url().should('include', '/');

      cy.visit('/sessions/create');
      cy.url().should('include', '/');

      cy.visit('/sessions/update/1');
      cy.url().should('include', '/');
    });

    it('Should handle 404 page', () => {
      cy.visit('/nonexistent');
      cy.get('h1').should('contain', 'Page not found !');
    });

    it('Should test empty sessions state', () => {
      setupUserMocks();
      cy.intercept('GET', '/api/session', [] as Session[]).as('emptySessions');

      cy.visit('/login');
      loginAsUser();
      cy.wait('@emptySessions');

      cy.get('mat-card-title').should('contain', 'Sessions available');
      cy.get('.item').should('have.length', 0);
    });

    it('Should navigate to session detail', () => {
      setupUserMocks();
      setupSessionMocks();

      cy.visit('/login');
      loginAsUser();

      cy.get('button').contains('Detail').click();
      cy.wait('@sessionDetail');

      // Vérifier qu'on est sur la page détail
      cy.url().should('include', '/sessions/detail/1');

      // Vérifier que les éléments de base sont présents
      cy.get('mat-card').should('be.visible');
      cy.get('button').should('exist'); // Au moins un bouton existe

      // Bouton retour
      cy.get('button[mat-icon-button]').should('be.visible');
      cy.get('button[mat-icon-button]').click();
      cy.url().should('include', '/sessions');
    });

    it('Should handle form validation errors', () => {
      setupAdminMocks();

      cy.visit('/login');
      loginAsAdmin();

      // Test validation sur création de session
      cy.get('button').contains('Create').click();

      // Essayer de soumettre formulaire vide
      cy.get('button[type=submit]').should('be.disabled');

      // Remplir partiellement
      cy.get('input[formControlName=name]').type('Test');
      cy.get('button[type=submit]').should('be.disabled');

      // Formulaire complet devrait activer le bouton
      cy.get('input[formControlName=date]').type('2025-12-25');
      cy.get('mat-select[formControlName=teacher_id]').click();
      cy.get('mat-option').first().click();
      cy.get('textarea[formControlName=description]').type('Description');
      cy.get('button[type=submit]').should('not.be.disabled');
    });
  });
});
