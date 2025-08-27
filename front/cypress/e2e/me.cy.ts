describe('Me spec', () => {
  it('Should display user profile', () => {
    // Mock connexion
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false,
      },
    });

    // Mock pour récupérer les infos utilisateur
    cy.intercept('GET', '/api/user/1', {
      id: 1,
      firstName: 'Test',
      lastName: 'User',
      email: 'test@test.com',
      admin: false,
      createdAt: '2025-02-10T00:00:00Z',
      updatedAt: '2025-05-15T00:00:00Z',
    }).as('getUserInfo');

    cy.intercept('GET', '/api/session', []).as('session');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

    // Attendre d'être sur /sessions
    cy.url().should('include', '/sessions');

    // Cliquer sur Account
    cy.get('span').contains('Account').click();

    // Vérifications
    cy.url().should('include', '/me');
    cy.wait('@getUserInfo');
    cy.get('h1').should('contain', 'User information');
    cy.get('p').should('contain', 'Test USER');
    cy.get('p').should('contain', 'test@test.com');

    // Tester le bouton Delete
    cy.get('button').contains('Delete').should('be.visible');

    // Mock pour la suppression
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200,
    }).as('deleteUser');

    // Cliquer sur Delete
    cy.get('button').contains('Delete').click();

    // Vérifier redirection vers home après suppression
    cy.wait('@deleteUser');
    cy.url().should('not.include', '/me');
  });

  it('Should display admin profile without delete button', () => {
    // Mock connexion ADMIN
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 2,
        username: 'adminUser',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
      },
    });

    // Mock infos utilisateur admin
    cy.intercept('GET', '/api/user/2', {
      id: 2,
      firstName: 'Admin',
      lastName: 'User',
      email: 'admin@test.com',
      admin: true,
      createdAt: '2025-03-12T00:00:00Z',
      updatedAt: '2025-06-15T00:00:00Z',
    }).as('getAdminInfo');

    cy.intercept('GET', '/api/session', []).as('session');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

    // Aller sur Account
    cy.get('span').contains('Account').click();

    // Vérifications admin
    cy.wait('@getAdminInfo');
    cy.get('p').should('contain', 'You are admin');
    cy.get('button').contains('Delete').should('not.exist');

    // Tester le bouton Back
    cy.get('button[mat-icon-button]').click();
    cy.url().should('include', '/sessions');
  });
});
