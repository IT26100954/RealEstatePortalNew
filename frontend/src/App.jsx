import React, { useState } from 'react';
import { SessionProvider, useSession } from './shared/Session.jsx';
import { usePath, navigate } from './shared/navigation.js';
import { Notice } from './shared/Forms.jsx';
import ModulePages from './shared/ModulePages.jsx';
import UserPages from './modules/user/UserPages.jsx';
import property from './modules/property/config.js';
import moving from './modules/moving/config.js';
import appointment from './modules/appointment/config.js';
import offer from './modules/offer/config.js';
import review from './modules/review/config.js';
const modules = [property,moving,appointment,offer,review];
function Portal() {
 const path = usePath(); const clean = path.split('?')[0];
 const segments = clean.split('/').filter(Boolean);
 const session = useSession(); const [actionError,setActionError] = useState('');
 const config = modules.find(function(c) { return c.key === segments[0]; });
 async function logout() { setActionError(''); try { await session.logout(); navigate('/'); } catch(e) { setActionError(e.message); } }
 // Query values prefill forms; they never identify the signed-in actor.
 const params = new URLSearchParams(path.split('?')[1] || '');
 return <><a className="skip-link" href="#main-content">Skip to content</a><header className="site-header"><a href="#/" className="brand"><span className="brand-icon">R</span><span>Estate<span className="brand-accent">Portal</span></span></a><nav aria-label="Main navigation"><a href="#/property">Properties</a><a href="#/moving">Moving services</a><a href="#/review">Reviews</a>{session.user && <><a href="#/appointment">Viewings</a><a href="#/offer">Offers</a></>}{session.user?.role === 'ADMIN' && <a href="#/users">Accounts</a>}</nav><div className="account-nav">{session.user ? <><a href="#/profile">{session.user.username}</a><button className="secondary" onClick={logout}>Log out</button></> : <><a href="#/login">Log in</a><a className="button" href="#/register">Register</a></>}</div></header>
 <main id="main-content">{session.loading && <p role="status">Checking your session…</p>}{session.error && <Notice error>{session.error} <button className="text-button" onClick={session.refresh}>Refresh session</button></Notice>}{actionError && <Notice error>{actionError}</Notice>}
 {!segments.length ? <><section className="hero"><div><p className="eyebrow">A PLACE FOR YOUR NEXT CHAPTER</p><h1>Find a home.<br/>Make your move.</h1><p>Explore properties, arrange a viewing and manage your next move in one place.</p><div className="actions"><a className="button" href="#/property">Explore properties →</a><a className="button secondary" href="#/moving">Find moving services</a></div></div><div className="hero-art" aria-hidden="true"><div className="roof"/><div className="house"><i/><i/><b/></div><div className="sun"/></div></section><section><p className="eyebrow">EVERY STEP, CONNECTED</p><h2>Everything for your property journey</h2><div className="module-grid">{modules.map(function(c,index) { return <a className="module-card" href={'#/' + c.key} key={c.key}><span className="number">0{index+1}</span><h3>{c.title}</h3><p>{({property:'Discover your next home or manage your listings.',moving:'Find companies serving your next destination.',appointment:'Arrange and manage property viewings.',offer:'Submit offers and track seller decisions.',review:'Share experiences and read property feedback.'})[c.key]}</p><span>Open module →</span></a>; })}<a className="module-card" href={session.user ? '#/profile' : '#/register'}><span className="number">06</span><h3>Your account</h3><p>Manage your profile and account access.</p><span>Manage account →</span></a></div></section></> : config ? <ModulePages key={config.key + clean + (params.get('propertyId') || '')} config={config} segments={segments} initialPropertyId={params.get('propertyId') || ''} /> : ['login','register','profile','users'].includes(segments[0]) ? <UserPages key={clean} segments={segments} /> : <section className="panel"><h1>Page not found</h1><a href="#/">Return home</a></section>}
 </main><footer>EstatePortal · Properties, viewings & moving services<span>Real Estate Portal · Six-member project</span></footer></>;
}
export default function App() { return <SessionProvider><Portal /></SessionProvider>; }
