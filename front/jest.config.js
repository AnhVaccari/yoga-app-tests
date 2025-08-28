module.exports = {
  moduleNameMapper: {
    '@core/(.*)': '<rootDir>/src/app/core/$1',
  },
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  testEnvironment: 'jsdom',
  bail: false,
  verbose: false,
  collectCoverage: true,
  coverageDirectory: './coverage',
  collectCoverageFrom: [
    'src/app/**/*.ts',
    '!src/app/**/*.spec.ts',
    '!src/app/**/*.d.ts',
    '!src/app/**/app-routing.module.ts',
    '!src/app/**/auth-routing.module.ts',
    '!src/app/**/sessions-routing.module.ts',
    '!src/app/**/*.module.ts',
    '!src/app/guards/**',
    '!src/app/interceptors/**',
    '!src/environments/**',
    '!src/main.ts',
    '!src/polyfills.ts'
  ],
  coverageReporters: ['html', 'lcov', 'text'],
  testPathIgnorePatterns: ['<rootDir>/node_modules/'],
  coveragePathIgnorePatterns: ['<rootDir>/node_modules/'],
  coverageThreshold: {
    global: {
      statements: 80
    },
  },
  roots: [
    "<rootDir>"
  ],
  modulePaths: [
    "<rootDir>"
  ],
  moduleDirectories: [
    "node_modules"
  ],
  testMatch: [
    "**/?(*.)+(spec|test).[tj]s?(x)"
  ],
};
