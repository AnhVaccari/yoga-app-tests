describe('Register spec', () => {
  it('Register successful', () => {
    cy.intercept('POST', 'api/auth/register', {
      statusCode: 200,
    });

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@test.com');
    cy.get('input[formControlName=password]').type('password123');

    cy.get('button[type=submit]').click();

    cy.url().should('include', '/login');
  });

  it('Register with error', () => {
    cy.intercept('POST', 'api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' },
    });

    cy.visit('/register');

    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('existing@test.com');
    cy.get('input[formControlName=password]').type('password123');

    cy.get('button[type=submit]').click();

    cy.get('.error').should('be.visible');
  });

  it('Should show error when form is invalid', () => {
    // Mock qui échoue pour tester onError = true
    cy.intercept('POST', 'api/auth/register', {
      statusCode: 400,
      body: { message: 'Server error' },
    });

    cy.visit('/register');

    // Remplir avec des données VALIDES mais qui vont échouer
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('test@test.com');
    cy.get('input[formControlName=password]').type('password123');

    // Submit va déclencher l'erreur du serveur
    cy.get('button[type=submit]').click();

    // Vérifier que onError = true s'affiche
    cy.get('.error').should('be.visible');
    cy.contains('An error occurred').should('be.visible');

    cy.url().should('include', '/register');
  });

  it('Should test register validation and errors', () => {
    cy.visit('/register');

    // Test 1: Erreur serveur avec formulaire valide
    cy.intercept('POST', 'api/auth/register', {
      statusCode: 400,
      body: { message: 'Server error' },
    }).as('registerError');

    // Remplir avec données valides
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john@test.com');
    cy.get('input[formControlName=password]').type('password123');

    // Le bouton est maintenant activé
    cy.get('button[type=submit]').should('not.be.disabled');
    cy.get('button[type=submit]').click();

    cy.wait('@registerError');
    cy.get('.error').should('be.visible');
    cy.contains('An error occurred').should('be.visible');
  });
});
