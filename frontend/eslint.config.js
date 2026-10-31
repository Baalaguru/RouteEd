import js from '@eslint/js'
import globals from 'globals'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'

export default [
  { ignores: ['dist', 'node_modules'] },
  js.configs.recommended,
  { files: ['**/*.{ts,tsx}'], languageOptions: { ecmaVersion: 2020, globals: globals.browser } },
  reactHooks.configs.flat['recommended-latest'],
  reactRefresh.configs.vite,
]
