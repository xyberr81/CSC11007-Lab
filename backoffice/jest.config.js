const nextJest = require('next/jest')

const createJestConfig = nextJest({
  dir: './',
})

const customJestConfig = {
  setupFilesAfterEnv: ['<rootDir>/jest.setup.js'],
  testEnvironment: 'jest-environment-jsdom',
  moduleDirectories: ['node_modules', '<rootDir>/'],
  // NOTE: These moduleNameMapper paths MUST match tsconfig.json compilerOptions.paths
  // Verify: npx tsc --showConfig
  moduleNameMapper: {
    '^@commonServices/(.*)$': '<rootDir>/common/services/$1',
    '^@commonItems/(.*)$': '<rootDir>/common/items/$1',
    '^@constants/(.*)$': '<rootDir>/constants/$1',
    '^@catalogModels/(.*)$': '<rootDir>/modules/catalog/models/$1',
    '^@catalogServices/(.*)$': '<rootDir>/modules/catalog/services/$1',
    '^@catalogComponents/(.*)$': '<rootDir>/modules/catalog/components/$1',
    '^@locationComponents/(.*)$': '<rootDir>/modules/location/components/$1',
    '^@locationModels/(.*)$': '<rootDir>/modules/location/models/$1',
    '^@locationServices/(.*)$': '<rootDir>/modules/location/services/$1',
    '^@taxServices/(.*)$': '<rootDir>/modules/tax/services/$1',
    '^@taxComponents/(.*)$': '<rootDir>/modules/tax/components/$1',
    '^@taxModels/(.*)$': '<rootDir>/modules/tax/models/$1',
    '^@inventoryServices/(.*)$': '<rootDir>/modules/inventory/services/$1',
    '^@inventoryModels/(.*)$': '<rootDir>/modules/inventory/models/$1',
    '^@inventoryComponents/(.*)$': '<rootDir>/modules/inventory/components/$1',
    '^@webhookComponents/(.*)$': '<rootDir>/modules/webhook/components/$1',
    '^@webhookServices/(.*)$': '<rootDir>/modules/webhook/services/$1',
    '^@webhookModels/(.*)$': '<rootDir>/modules/webhook/models/$1',
  },
  coverageReporters: ['json-summary', 'text', 'lcov'],
}

module.exports = createJestConfig(customJestConfig)
