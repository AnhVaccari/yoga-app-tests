describe('Sessions spec', () => {
  it('Should display sessions list', () => {
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

    //cy.intercept('GET', '/api/session', []).as('session');

    // Mock sessions list
    cy.intercept('GET', '/api/session', [
      {
        id: 1,
        name: 'Yoga relaxation',
        date: '2025-08-08',
        description: 'Session de relaxation pour débutants',
      },
      {
        id: 2,
        name: 'Yoga avancé',
        date: '2025-07-31',
        description: 'Session intensive pour experts',
      },
    ]).as('sessions');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test123');
    cy.get('button[type=submit]').click();

    // Vérifications
    cy.wait('@sessions');
    cy.get('mat-card-title').should('contain', 'Sessions available');
    cy.get('.item').should('have.length', 2);
    cy.get('.item').first().should('contain', 'Yoga relaxation');
    cy.get('.item').last().should('contain', 'Yoga avancé');
  });

  it('Should navigate to session detail', () => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'testUser',
        firstName: 'Test',
        lastName: 'User',
        admin: false,
      },
    });

    cy.intercept('GET', '/api/session/1', {
      id: 1,
      name: 'Yoga relaxation',
      date: '2024-01-15',
      description: 'Session de relaxation pour débutants',
      teacher_name: 'John Doe',
      users: [],
    }).as('sessionDetail');

    // Mock session detail
    cy.intercept('GET', '/api/session', [
      {
        id: 1,
        name: 'Yoga relaxation',
        date: '2025-08-08',
        description: 'Session de relaxation pour débutants',
      },
    ]).as('sessions');

    cy.visit('/login');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('test123');
    cy.get('button[type=submit]').click();

    // Cliquer sur Detail
    cy.get('button').contains('Detail').click();

    // Vérification
    cy.url().should('include', '/sessions/detail/1');
  });
});
