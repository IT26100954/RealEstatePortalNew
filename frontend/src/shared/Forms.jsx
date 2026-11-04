import React, { useState } from 'react';
export function Notice({ error, children }) { return <p className={error ? 'notice error' : 'notice success'} role={error ? 'alert' : 'status'}>{children}</p>; }
export function Field({ field, value, onChange }) {
  const props = { id: field.name, name: field.name, 'aria-label': field.label, value: value ?? '', required: !field.optional,
    disabled: field.disabled, maxLength: field.max, onChange: function (e) { onChange(field.name, e.target.value); } };
  return <label className="field" htmlFor={field.name}><span>{field.label}</span>
    {field.options ? <select {...props}>{field.options.map(function (option) { return <option key={option} value={option}>{option}</option>; })}</select> :
      field.type === 'textarea' ? <textarea {...props} rows="4" /> :
      <input {...props} type={field.type || 'text'} min={field.min} max={field.maximum} step={field.step} autoComplete={field.autoComplete || 'off'} />}
    {field.help && <small>{field.help}</small>}</label>;
}
export function RecordForm({ fields, initial = {}, submit, label = 'Save', cancel }) {
  const [values, setValues] = useState(function () {
    const data = {};
    fields.forEach(function (f) { data[f.name] = initial[f.name] ?? (f.options ? f.options[0] : ''); });
    return data;
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  function change(name, value) { setValues(function (old) { return { ...old, [name]: value }; }); }
  async function handle(e) {
    e.preventDefault(); setBusy(true); setError('');
    try {
      const body = {};
      fields.forEach(function (f) {
        let v = values[f.name];
        if (f.type !== 'password' && typeof v === 'string') v = v.trim();
        if (!f.optional && v === '') throw new Error(f.label + ' is required.');
        if (f.money && (!/^\d+(\.\d{1,2})?$/.test(v) || Number(v) <= 0 || Number(v) >= 10000000000000)) throw new Error('Enter a positive amount with at most two decimal places.');
        if (f.type === 'password' && (v.length < 8 || v.length > 128)) throw new Error('Passwords must contain 8 to 128 characters.');
        if (f.name === 'viewingTime' && v.length === 5) v += ':00';
        body[f.name] = f.numeric ? Number(v) : v;
      });
      await submit(body);
    } catch (e) { setError(e.message); } finally { setBusy(false); }
  }
  return <form onSubmit={handle} className="record-form"><fieldset disabled={busy}>
    {fields.map(function (f) { return <Field key={f.name} field={f} value={values[f.name]} onChange={change} />; })}
    {error && <Notice error>{error}</Notice>}
    <div className="actions"><button type="submit">{busy ? 'Saving…' : label}</button>{cancel && <button type="button" className="secondary" onClick={cancel}>Cancel</button>}</div>
  </fieldset></form>;
}
