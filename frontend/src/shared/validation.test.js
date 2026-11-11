import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import { validateEmail, validatePhone, validatePassword } from './validation.js';
const cases = JSON.parse(fs.readFileSync(new URL('./validation-cases.json', import.meta.url), 'utf8'));
const rules = { email: validateEmail, phone: validatePhone, password: validatePassword };
for (const [name, rule] of Object.entries(rules)) {
  test(name + ' accepts valid values unchanged', function () {
    for (const value of cases[name].valid) assert.equal(rule(value), value);
  });
  test(name + ' rejects invalid values', function () {
    for (const value of cases[name].invalid) assert.throws(function () { rule(value); }, Error);
    assert.throws(function () { rule(null); }, Error);
  });
}
