import React, { useEffect, useState } from 'react';
import { api } from '../../shared/api.js';
import { useSession } from '../../shared/Session.jsx';
import { navigate } from '../../shared/navigation.js';
import { Notice, RecordForm } from '../../shared/Forms.jsx';
const profile = [
 { name:'username', label:'Username', max:40 },
 { name:'email', label:'Email', type:'email', max:254 },
 { name:'phone', label:'Phone', type:'tel', max:20 }
];
const password = { name:'password', label:'Password', type:'password', max:128, help:'8–128 characters', autoComplete:'new-password' };
const role = { name:'role', label:'Role', options:['BUYER','SELLER','ADMIN'] };

export default function UserPages({ segments }) {
 const session = useSession(); const { user } = session;
 const route = segments[0]; const id = segments[1]; const edit = segments[2] === 'edit';
 const [rows,setRows] = useState([]); const [record,setRecord] = useState(null);
 const [error,setError] = useState(''); const [notice,setNotice] = useState('');
 const [busy,setBusy] = useState(false); const [version,setVersion] = useState(0);
 useEffect(function() {
   setError(''); setNotice(''); setRecord(null); setRows([]);
   if (route !== 'users' || !user || user.role !== 'ADMIN' || id === 'new') return;
   const abort = new AbortController(); setBusy(true);
   api('/users' + (id ? '/' + encodeURIComponent(id) : ''), 'GET', undefined, abort.signal)
    .then(function(data) { if(id) setRecord(data); else { if(!Array.isArray(data)) throw new Error('Expected an account array.'); setRows(data); } })
    .catch(function(e) { if(e.name !== 'AbortError') setError(e.message); })
    .finally(function() { if(!abort.signal.aborted) setBusy(false); });
   return function() { abort.abort(); };
 }, [route,id,user,version]);
 if (route === 'login') return <section className="panel form-panel"><p className="eyebrow">WELCOME BACK</p><h1>Log in</h1><RecordForm fields={[{name:'username',label:'Username',max:40,autoComplete:'username'}, {name:'password',label:'Password',type:'password',max:128,autoComplete:'current-password'}]} submit={async function(body) { await session.login(body); navigate('/profile'); }} label="Log in" /><p>New here? <a href="#/register">Create an account</a></p></section>;
 if (route === 'register') return <section className="panel form-panel"><p className="eyebrow">GET STARTED</p><h1>Create your account</h1>{notice && <Notice>{notice} <a href="#/login">Log in</a></Notice>}<RecordForm fields={[profile[0],password,profile[1],profile[2],{...role,options:['BUYER','SELLER']}]} submit={async function(body) { await api('/auth/register','POST',body); setNotice('Account created. You can now log in.'); }} label="Register" /></section>;
 if (!user) return <section className="panel"><h1>Account access</h1><p>Please log in to continue.</p><a className="button" href="#/login">Log in</a></section>;
 if (route === 'profile') return <section><div className="page-heading"><div><p className="eyebrow">YOUR ACCOUNT</p><h1>My profile</h1></div><span className="badge">{user.role}</span></div><div className="two-column"><div className="panel"><h2>Profile details</h2>{notice && <Notice>{notice}</Notice>}<RecordForm key={user.id + user.username} fields={profile} initial={user} submit={async function(body) { await api('/users/' + user.id,'PUT',body); await session.refresh(); setNotice('Profile updated.'); }} label="Save profile" /></div><div className="panel"><h2>Change password</h2><p className="muted">Changing your password signs out existing sessions.</p><RecordForm fields={[{name:'currentPassword',label:'Current password',type:'password',max:128,autoComplete:'current-password'}, {name:'newPassword',label:'New password',type:'password',max:128,autoComplete:'new-password'}]} submit={async function(body) { await api('/users/' + user.id + '/password','PUT',body); session.forget(); navigate('/login'); }} label="Change password" /></div></div></section>;
 if (user.role !== 'ADMIN') return <Notice error>Only ADMIN accounts can access account management.</Notice>;
 async function save(body) { const result = await api('/users' + (id && id !== 'new' ? '/' + encodeURIComponent(id) : ''), id && id !== 'new' ? 'PUT' : 'POST',body); navigate('/users/' + result.id); setVersion(function(v) { return v+1; }); }
 async function remove() { if(!window.confirm('Permanently delete this account? Referenced accounts cannot be deleted.')) return; setBusy(true); setError(''); try { await api('/users/' + encodeURIComponent(id),'DELETE'); navigate('/users'); setVersion(function(v) { return v+1; }); } catch(e) { setError(e.message); } finally { setBusy(false); } }
 return <section><div className="page-heading"><div><p className="eyebrow">ADMINISTRATION</p><h1>{id === 'new' ? 'Create account' : edit ? 'Edit account' : 'Account management'}</h1></div><a className="button secondary" href="#/users">All accounts</a>{!id && <a className="button" href="#/users/new">Create account</a>}</div>
 {error && <Notice error>{error}</Notice>}{notice && <Notice>{notice}</Notice>}
 {busy ? <p role="status">Loading…</p> : id === 'new' ? <div className="panel form-panel"><RecordForm fields={[profile[0],password,profile[1],profile[2],role]} submit={save} cancel={function() { navigate('/users'); }} /></div> : id ? record && (edit ? <div className="panel form-panel"><RecordForm fields={profile} initial={record} submit={save} cancel={function() { navigate('/users/' + id); }} /></div> : <div className="panel"><h2>{record.username}</h2><dl>{['id','username','email','phone','role'].map(function(key) { return <div key={key}><dt>{key}</dt><dd>{record[key]}</dd></div>; })}</dl><div className="actions"><a className="button" href={'#/users/' + id + '/edit'}>Edit profile</a>{id !== user.id && <button className="danger" onClick={remove}>Delete account</button>}</div><h2>Change role</h2><p className="muted">Referenced accounts and the last administrator are protected by the backend.</p><RecordForm key={record.id + record.role} fields={[role]} initial={record} submit={async function(body) { const result = await api('/users/' + encodeURIComponent(id) + '/role','PUT',body); setRecord(result); if(id === user.id) await session.refresh(); setNotice('Role updated.'); }} label="Update role" /></div>) : <div className="table-wrap"><table><thead><tr><th>Username</th><th>Email</th><th>Role</th><th>Action</th></tr></thead><tbody>{rows.map(function(row) { return <tr key={row.id}><td>{row.username}</td><td>{row.email}</td><td><span className="badge">{row.role}</span></td><td><a href={'#/users/' + row.id}>Manage →</a></td></tr>; })}</tbody></table>{!error && rows.length===0 && <p className="empty">No accounts found.</p>}</div>}
 </section>;
}
