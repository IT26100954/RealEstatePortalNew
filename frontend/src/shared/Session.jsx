import React, { createContext, useContext, useEffect, useState } from 'react';
import { api, clearToken, getSession } from './api.js';
const Context = createContext(null);
export function SessionProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  async function refresh() {
    setLoading(true); setError('');
    try { const value = await getSession(); setUser(value); return value; }
    catch (e) { setUser(null); setError(e.message); return null; }
    finally { setLoading(false); }
  }
  useEffect(function () { refresh(); }, []);
  async function login(body) { const result = await api('/auth/login', 'POST', body); setUser(result.user); setError(''); }
  async function logout() { await api('/auth/logout', 'POST'); setUser(null); clearToken(); }
  function forget() { setUser(null); clearToken(); }
  return <Context.Provider value={{ user, loading, error, refresh, login, logout, forget }}>{children}</Context.Provider>;
}
export function useSession() { return useContext(Context); }
