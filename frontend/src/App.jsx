import React from 'react';

// Shared setup shell only. Module owners will write their own screens.
const modules = [
  ['01', 'User and Access', 'user'],
  ['02', 'Property Listings', 'property'],
  ['03', 'Moving Services', 'moving'],
  ['04', 'Viewing Appointments', 'appointment'],
  ['05', 'Offers', 'offer'],
  ['06', 'Reviews and Feedback', 'review'],
];

export default function App() {
  return (
    <main>
      <header>
        <p className="eyebrow">SIX MEMBER TEAM PROJECT</p>
        <h1>Real Estate Portal</h1>
        <p className="intro">The shared frontend is ready for module development.</p>
      </header>
      <section aria-labelledby="modules-heading">
        <h2 id="modules-heading">Module responsibilities</h2>
        <p>These are placeholders. Account features, forms and CRUD screens are not implemented.</p>
        <div className="modules">
          {modules.map(([number, name, folder]) => (
            <article key={folder}>
              <span className="number">{number}</span>
              <h3>{name}</h3>
              <p>Awaiting member implementation</p>
              <code>src/modules/{folder}/</code>
            </article>
          ))}
        </div>
      </section>
      <footer>Shared foundation · React + Vite · Spring Boot + MySQL</footer>
    </main>
  );
}
