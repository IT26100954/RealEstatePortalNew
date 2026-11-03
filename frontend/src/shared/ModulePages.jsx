import React, { useEffect, useState } from 'react';
import { api } from './api.js';
import { useSession } from './Session.jsx';
import { navigate } from './navigation.js';
import { Notice, RecordForm } from './Forms.jsx';

function human(value) { return value.replace(/([A-Z])/g, ' $1').replace(/^./, function (c) { return c.toUpperCase(); }); }
function owned(config, record, user) { return !!user && record[config.owner] === user.id; }
function canCreate(config, user) {
  if (!user) return false;
  if (config.key === 'property') return user.role === 'SELLER';
  if (config.key === 'moving') return user.role === 'ADMIN';
  if (config.key === 'review') return user.role === 'BUYER' || user.role === 'SELLER';
  return user.role === 'BUYER';
}
function canEdit(config, record, user) {
  if (!user) return false;
  if (config.key === 'moving') return user.role === 'ADMIN';
  if (config.key === 'property') return owned(config, record, user) || user.role === 'ADMIN';
  if (config.key === 'review') return owned(config, record, user);
  return owned(config, record, user) && record.status === 'PENDING';
}
function canDelete(config, record, user) {
  if (!user) return false;
  if (config.key === 'moving') return user.role === 'ADMIN';
  const permitted = owned(config, record, user) || user.role === 'ADMIN';
  return permitted && (!['appointment','offer'].includes(config.key) || record.status === 'PENDING');
}
function display(name, value, propertyNames) {
  if (name === 'propertyId') return propertyNames[value] || value;
  if (name === 'price' || name === 'amount') return 'LKR ' + String(value);
  return value === null || value === undefined || value === '' ? '—' : String(value);
}
export default function ModulePages({ config, segments, initialPropertyId = '' }) {
  const { user } = useSession();
  const mode = segments[1] || 'list';
  const id = mode === 'new' || mode === 'list' ? null : mode;
  const editing = segments[2] === 'edit';
  const [records, setRecords] = useState([]);
  const [record, setRecord] = useState(initialPropertyId ? { propertyId: initialPropertyId } : null);
  const [properties, setProperties] = useState([]);
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(true);
  const [actionBusy, setActionBusy] = useState(false);
  const [version, setVersion] = useState(0);
  const [query, setQuery] = useState('');
  const [city, setCity] = useState('');
  const [filters, setFilters] = useState('');
  const [lookupWarning, setLookupWarning] = useState('');
  const base = '/' + config.key;
  const endpoint = '/' + config.endpoint;
  useEffect(function () {
    const abort = new AbortController();
    setBusy(true); setError(''); setRecord(mode === 'new' && initialPropertyId ? { propertyId: initialPropertyId } : null);
    if (!config.public && !user) { setBusy(false); return function () { abort.abort(); }; }
    async function load() {
      try {
        if (id) setRecord(await api(endpoint + '/' + encodeURIComponent(id), 'GET', undefined, abort.signal));
        else if (mode !== 'new') {
          const rows = await api(endpoint + filters, 'GET', undefined, abort.signal);
          if (!Array.isArray(rows)) throw new Error('The list endpoint must return an array.');
          setRecords(rows);
        }
      } catch (e) { if (e.name !== 'AbortError') setError(e.message); }
      finally { if (!abort.signal.aborted) setBusy(false); }
    }
    load(); return function () { abort.abort(); };
  }, [config, id, mode, endpoint, filters, version, user]);
  useEffect(function () {
    if (!['appointment','offer','review'].includes(config.key)) return;
    const abort = new AbortController();
    api('/properties', 'GET', undefined, abort.signal).then(function (rows) {
      if (Array.isArray(rows)) { setProperties(rows); setLookupWarning(''); }
    }).catch(function (e) { if (e.name !== 'AbortError') setLookupWarning('Property choices are unavailable. You can enter a property ID instead.'); });
    return function () { abort.abort(); };
  }, [config.key, version, user]);
  const names = {};
  properties.forEach(function (p) { names[p.id] = p.title + ' — ' + p.city; });
  const [propertyOwner, setPropertyOwner] = useState(null);
  useEffect(function () {
    setPropertyOwner(null);
    if (!record || !record.propertyId || !user || user.role !== 'SELLER') return;
    const abort = new AbortController();
    api('/properties/' + encodeURIComponent(record.propertyId), 'GET', undefined, abort.signal)
      .then(function (p) { setPropertyOwner(p.ownerId); }).catch(function () {});
    return function () { abort.abort(); };
  }, [record, user]);
  if (!config.public && !user) return <section className="panel"><h1>{config.title}</h1><p>Log in to view your records.</p><a className="button" href="#/login">Log in</a></section>;
  async function save(body) {
    if (config.key === 'review') body.rating = Number(body.rating);
    const result = await api(endpoint + (id ? '/' + encodeURIComponent(id) : ''), id ? 'PUT' : 'POST', body);
    if (!result || !result.id) throw new Error('Save succeeded but the backend did not return the record ID. Refresh the list.');
    setVersion(function (v) { return v + 1; }); navigate(base + '/' + result.id);
  }
  async function remove() {
    if (!window.confirm('Permanently delete this ' + config.singular + '? Referenced records may be protected.')) return;
    setActionBusy(true); setError('');
    try { await api(endpoint + '/' + encodeURIComponent(id), 'DELETE'); setVersion(function (v) { return v + 1; }); navigate(base); }
    catch (e) { setError(e.message); } finally { setActionBusy(false); }
  }
  async function changeStatus(status) {
    if (!window.confirm('Change status to ' + status + '?')) return;
    setActionBusy(true); setError('');
    try { setRecord(await api(endpoint + '/' + encodeURIComponent(id) + '/status', 'PATCH', { status })); }
    catch (e) { setError(e.message); } finally { setActionBusy(false); }
  }
  function fields() {
    return config.fields.filter(function (f) { return !(id && f.name === 'propertyId'); }).map(function (f) {
      if (f.type !== 'property') return f;
      return { ...f, type: 'text', help: 'Choose a listed property, or paste its ID if not listed.' };
    });
  }
  function statuses() {
    if (!record || !user) return [];
    const buyer = record.buyerId === user.id;
    const pending = record.status === 'PENDING';
    const result = [];
    if (config.key === 'appointment') {
      if ((buyer || user.role === 'ADMIN') && ['PENDING','CONFIRMED'].includes(record.status)) result.push('CANCELLED');
      // Only owning sellers may see confirmation controls. A private property
      // lookup below supplies ownership, rather than trusting a client input.
      if (user.role === 'SELLER' && propertyOwner === user.id) {
        if (pending) result.push('CONFIRMED','REJECTED');
        if (record.status === 'CONFIRMED') result.push('CANCELLED','COMPLETED');
      }
    }
    if (config.key === 'offer' && pending) {
      if (buyer) result.push('WITHDRAWN');
      if (user.role === 'SELLER' && propertyOwner === user.id) result.push('ACCEPTED','REJECTED');
    }
    return [...new Set(result)];
  }
  return <section>
    <div className="page-heading"><div><p className="eyebrow">YOUR REAL ESTATE PORTAL</p><h1>{mode === 'new' ? 'Add ' + config.singular : editing ? 'Edit ' + config.singular : config.title}</h1></div>
      <div className="actions">{(id || mode === 'new') && <a className="button secondary" href={'#' + base}>Back to list</a>}
        {!id && mode !== 'new' && canCreate(config,user) && <a className="button" href={'#' + base + '/new'}>Add {config.singular}</a>}</div></div>
    {error && <Notice error>{error} <button className="text-button" onClick={function () { setVersion(function(v) { return v + 1; }); }}>Retry</button></Notice>}
    {busy ? <p role="status">Loading…</p> : mode === 'new' || editing ?
      (!canCreate(config,user) && !id) || (id && record && !canEdit(config,record,user)) ? <Notice error>You cannot edit this record.</Notice> :
      (!id || record) && <div className="panel form-panel">
        {lookupWarning && <Notice error>{lookupWarning}</Notice>}
        {!id && properties.length > 0 && ['appointment','offer','review'].includes(config.key) && <div className="property-picker"><h2>Available properties</h2><p>Select a property to fill the form below.</p>{properties.filter(function(p) { return p.status === 'AVAILABLE' && !(config.key === 'review' && user && p.ownerId === user.id); }).map(function(p) { return <button className="secondary" key={p.id} onClick={function () { setRecord({ propertyId: p.id }); }}>{p.title} · {p.city}</button>; })}</div>}
        <RecordForm key={(id || 'new') + (record?.propertyId || '')} fields={fields()} initial={record || {}} submit={save} cancel={function () { navigate(id ? base + '/' + id : base); }} />
      </div> : id ? record && <div className="panel"><div className="detail-title"><h2>{record.title || record.companyName || config.singular}</h2>{record.status && <span className="badge">{record.status}</span>}</div>
        <dl>{Object.entries(record).filter(function ([key]) { return key !== 'passwordHash' && key !== 'password'; }).map(function ([key,value]) { return <div key={key}><dt>{human(key)}</dt><dd>{display(key,value,names)}</dd></div>; })}</dl>
        <div className="actions">{canEdit(config,record,user) && <a className="button" href={'#' + base + '/' + id + '/edit'}>Edit</a>}
          {canDelete(config,record,user) && <button className="danger" disabled={actionBusy} onClick={remove}>Delete</button>}
          {statuses().map(function (status) { return <button className="secondary" key={status} disabled={actionBusy} onClick={function () { changeStatus(status); }}>{human(status.toLowerCase())}</button>; })}
          {config.key === 'property' && user?.role === 'BUYER' && record.status === 'AVAILABLE' && <><a className="button secondary" href={'#/appointment/new?propertyId=' + id}>Request viewing</a><a className="button secondary" href={'#/offer/new?propertyId=' + id}>Make offer</a></>}
          {config.key === 'property' && user && ['BUYER','SELLER'].includes(user.role) && record.ownerId !== user.id && <a className="button secondary" href={'#/review/new?propertyId=' + id}>Write review</a>}
        </div><p className="muted">Actions are also checked by the server. Protected records cannot be deleted.</p></div> :
      <>{config.key === 'property' && <form className="search-bar" onSubmit={function(e) { e.preventDefault(); const params = new URLSearchParams(); if(query.trim()) params.set('q',query.trim()); if(city.trim()) params.set('city',city.trim()); setFilters(params.size ? '?' + params.toString() : ''); }}><label>Search<input value={query} onChange={function(e) { setQuery(e.target.value); }} placeholder="Title or description" /></label><label>City<input value={city} onChange={function(e) { setCity(e.target.value); }} /></label><button>Search</button></form>}
        {!error && records.length === 0 && <div className="panel empty"><h2>No records yet</h2><p>{canCreate(config,user) ? 'Add your first ' + config.singular + ' to get started.' : 'Records will appear here when available.'}</p></div>}
        {records.length > 0 && <div className="table-wrap"><table><thead><tr>{config.columns.map(function(c) { return <th key={c}>{human(c)}</th>; })}<th>Action</th></tr></thead><tbody>{records.map(function(row) { return <tr key={row.id}>{config.columns.map(function(c) { return <td key={c}>{display(c,row[c],names)}</td>; })}<td><a href={'#' + base + '/' + row.id}>View details →</a></td></tr>; })}</tbody></table></div>}
      </>}
  </section>;
}
