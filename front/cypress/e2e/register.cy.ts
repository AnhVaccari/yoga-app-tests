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
});
