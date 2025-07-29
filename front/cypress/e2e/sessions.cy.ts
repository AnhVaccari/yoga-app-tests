describe('Sessions spec', () => {
  it('Should display sessions list', () => {
    // Mock connexion USER
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

  it('Should display admin buttons for admin user', () => {
    // Mock connexion ADMIN
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'adminUser',
        firstName: 'Admin',
        lastName: 'User',
        admin: true,
      },
    });

    // Mock sessions
    cy.intercept('GET', '/api/session', [
      {
        id: 1,
        name: 'Yoga relaxation',
        date: '2025-08-08',
        description: 'Session de relaxation pour débutants',
      },
    ]).as('sessions');

    // Mock teachers
    cy.intercept('GET', '/api/teacher', [
      {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
      },
      {
        id: 2,
        firstName: 'Sylvie',
        lastName: 'Yogi',
      },
    ]).as('teachers');

    // Se connecter
    cy.visit('/login');
    cy.get('input[formControlName=email]').type('admin@test.com');
    cy.get('input[formControlName=password]').type('admin123');
    cy.get('button[type=submit]').click();

    // Vérifications admin
    cy.get('button').contains('Create').should('be.visible');
    cy.get('button').contains('Edit').should('be.visible');

    // Test clic sur Create
    cy.get('button').contains('Create').click();
    cy.url().should('include', '/sessions/create');

    // Mock pour la création
    cy.intercept('POST', '/api/session', {
      statusCode: 201,
      body: {
        id: 3,
        name: 'Nouvelle session',
        date: '2024-02-01',
        description: 'Session créée par test',
      },
    }).as('createSession');

    // Remplir le formulaire
    cy.get('input[formControlName=name]').type('Session test');
    cy.get('input[formControlName=date]').type('2025-08-22');
    cy.get('mat-select[formControlName=teacher_id]').click();
    cy.get('mat-option').first().click();
    cy.get('textarea[formControlName=description]').type('Description de test');

    // Soumettre
    cy.get('button[type=submit]').click();

    // Vérifier redirection
    cy.url().should('include', '/sessions');
  });
});
