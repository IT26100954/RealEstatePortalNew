// Keep these practical contact rules aligned with Java InputRules.
export function validateEmail(value) {
  const fail = function () { throw new Error('Enter a valid email address.'); };
  if (typeof value !== 'string' || value.length > 254) fail();
  const at = value.indexOf('@');
  if (at <= 0 || at !== value.lastIndexOf('@')) fail();
  const local = value.slice(0, at);
  const labels = value.slice(at + 1).split('.');
  if (local.length > 64 || !/^[A-Za-z0-9!#$%&'*+/=?^_`{|}~.-]+$/.test(local) ||
      local.startsWith('.') || local.endsWith('.') || local.includes('..')) fail();
  if (labels.length < 2) fail();
  for (const label of labels) {
    if (!/^[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?$/.test(label)) fail();
  }
  if (!/^[A-Za-z]{2,63}$/.test(labels[labels.length - 1])) fail();
  return value;
}
export function validatePhone(value) {
  if (typeof value !== 'string' || !/^\+?[0-9]{7,15}$/.test(value)) {
    throw new Error('Enter 7 to 15 phone digits, optionally starting with +.');
  }
  return value;
}
export function validatePassword(value) {
  if (typeof value !== 'string' || value.length < 8 || value.length > 128 || /^[\s\u0085]*$/u.test(value)) {
    throw new Error('Password must contain 8 to 128 characters and cannot be only whitespace.');
  }
  return value;
}
