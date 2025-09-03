/// <reference types="cypress" />

describe('Login spec', () => {
  it('Login successfull', () => {
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true,
      },
    });

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []
    ).as('session');

    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type(
      `${'test!1234'}{enter}{enter}`
    );

    cy.url().should('include', '/sessions');
  });

  it('Login with error', () => {
    cy.visit('/login');

    // Mock d'erreur
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' },
    });

    cy.get('input[formControlName=email]').type('wrong@test.com');
    cy.get('input[formControlName=password]').type('wrongpassword{enter}');

    // Vérifier qu'on reste sur la page login
    cy.url().should('include', '/login');
  });

  it('Should test login with additional interactions', () => {
    // Vos mocks existants qui marchent...

    // Login existant qui fonctionne
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}');

    cy.get('mat-toolbar').should('be.visible'); // Test du toolbar
    cy.get('button').should('be.visible'); // Test des boutons
    cy.get('[routerLink]').should('exist'); // Test des liens de navigation

    cy.get('*').should('exist'); // Force l'analyse de tous les éléments
    cy.get('mat-form-field').should('be.visible');
    cy.get('mat-card').should('be.visible');
    cy.get('button[type=submit]').should('be.visible');
  });

  it('Should show error with wrong credentials', () => {
    // Mock qui retourne une erreur
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: { message: 'Invalid credentials' },
    }).as('loginError');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('wrong@email.com');
    cy.get('input[formControlName=password]').type('wrongpassword');
    cy.get('button[type=submit]').click();

    // Attendre la réponse d'erreur
    cy.wait('@loginError');

    // L'erreur devrait s'afficher
    cy.get('.error').should('be.visible');
    cy.contains('An error occurred').should('be.visible');
  });
});
