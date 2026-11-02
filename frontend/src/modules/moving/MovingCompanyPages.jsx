import React, {useEffect, useState} from 'react';
import {api} from '../../shared/api.js';
import {useSession} from '../../shared/Session.jsx';
import {go} from '../../shared/usePath.js';

const BASE = 'moving-companies';
const TITLE = 'Moving Services';
const fields = [
  {
    "key": "companyName",
    "label": "Company name",
    "type": "text"
  },
  {
    "key": "email",
    "label": "Email",
    "type": "email"
  },
  {
    "key": "phone",
    "label": "Phone",
    "type": "text"
  },
  {
    "key": "serviceAreas",
    "label": "Service areas",
    "type": "text"
  },
  {
    "key": "description",
    "label": "Description",
    "type": "text"
  },
  {
    "key": "availability",
    "label": "Availability",
    "type": "text",
    "options": [
      "AVAILABLE",
      "UNAVAILABLE"
    ]
  }
];
const actorFields = [];

export default function MovingCompanyPages({path}) {
  const {actor} = useSession();
  const [rows, setRows] = useState([]);
  const [record, setRecord] = useState(null);
  const [form, setForm] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const parts = path.split('/').filter(Boolean);
  const id = parts[1];
  const mode = id === 'new' ? 'form' : id ? (parts[2] === 'edit' ? 'form' : 'detail') : 'list';
  const creating = id === 'new';

  useEffect(() => {
    let active = true;
    setError('');
    setRecord(null);
    setBusy(true);

    async function load() {
      if (mode === 'list') {
        const data = await api('/' + BASE);
        if (active) setRows(data);
      } else if (creating) {
        const initial = Object.fromEntries(fields.map(f => [f.key, f.options?.[0] || '']));
        for (const key of actorFields) initial[key] = actor?.id || '';
        if (active) setForm(initial);
      } else {
        const data = await api('/' + BASE + '/' + id);
        if (active) {
          setRecord(data);
          setForm(data);
        }
      }
    }

    load().catch(e => active && setError(e.message)).finally(() => active && setBusy(false));
    return () => {
      active = false;
    };
  }, [path, actor?.id]);

  async function submit(event) {
    event.preventDefault();
    setError('');
    setBusy(true);
    try {
      const data = {...form};
      if ('rating' in data) data.rating = Number(data.rating);
      // Monetary inputs remain decimal strings, as agreed in the API contract.
      const saved = await api('/' + BASE + (creating ? '' : '/' + id), creating ? 'POST' : 'PUT', data);
      go('/' + BASE + '/' + saved.id);
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
    }
  }

  async function remove() {
    if (!confirm('Delete this eligible record?')) return;
    setError('');
    setBusy(true);
    try {
      await api('/' + BASE + '/' + id, 'DELETE');
      go('/' + BASE);
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
    }
  }

  function title(row) {
    return row.title || row.companyName || row.propertyId || row.id;
  }

  return <section>
    <h1>{TITLE}</h1>
    {error && <p role="alert">{error}</p>}
    {busy && <p>Loading or saving...</p>}
    {mode === 'list' && <>
      <h2>List</h2>
      {actor && <p><a href={'#/' + BASE + '/new'}>Create a record</a></p>}
      {!busy && rows.length === 0 && <p>No permitted records.</p>}
      <ul>{rows.map(row => <li key={row.id}>
        <a href={'#/' + BASE + '/' + row.id}>{title(row)}</a>
      </li>)}</ul>
    </>}
    {mode === 'detail' && record && <>
      <h2>Details</h2>
      <dl>{fields.map(f => <React.Fragment key={f.key}>
        <dt>{f.label}</dt>
        <dd>{String(record[f.key] ?? '')}</dd>
      </React.Fragment>)}</dl>
      {actor && <p><a href={'#/' + BASE + '/' + id + '/edit'}>Edit or manage</a></p>}
      {actor && <button disabled={busy} onClick={remove}>Delete eligible record</button>}
    </>}
    {mode === 'form' && <>
      <h2>{creating ? 'Create' : 'Edit or manage'}</h2>
      {!actor ? <p><a href="#/login">Log in first</a></p> : <form onSubmit={submit}>
        {fields.map(f => <label key={f.key}>{f.label}
          {f.options ? <select value={form[f.key] ?? ''} onChange={e => setForm({...form, [f.key]: e.target.value})}>
            {f.options.map(o => <option key={o} value={o}>{o}</option>)}
          </select> : <input type={f.type}
                             step={f.type === 'time' ? '1' : f.key === 'rating' ? '1' : f.type === 'number' ? '0.01' : undefined}
                             value={form[f.key] ?? ''}
                             readOnly={actorFields.includes(f.key) && !(BASE === 'properties' && actor.role === 'ADMIN' && creating)}
                             onChange={e => setForm({...form, [f.key]: e.target.value})}/>}
        </label>)}
        <button disabled={busy} type="submit">Save</button>
      </form>}
    </>}
    <p><a href={'#/' + BASE}>Back to list</a></p>
    <p>Actions are checked by the backend. A visible button is not permission.</p>
  </section>;
}
