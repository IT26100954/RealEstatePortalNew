let csrfToken = null;

export class ApiError extends Error {
  constructor(message, status) { super(message); this.status = status; }
}

async function send(path, method, body, signal) {
  const headers = { Accept: 'application/json' };
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  if (method !== 'GET') headers['X-CSRF-TOKEN'] = csrfToken || '';
  let response;
  try {
    response = await fetch('/api' + path, {
      method, headers, credentials: 'include', cache: 'no-store',
      body: body === undefined ? undefined : JSON.stringify(body), signal
    });
  } catch (error) {
    if (error.name === 'AbortError') throw error;
    throw new ApiError('Cannot reach the server. Check that the backend is running on port 8081.', 0);
  }
  const text = await response.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { /* handled below */ }
  if (!response.ok) {
    const fallback = response.status === 404 ? 'This endpoint or record is not available yet.' :
      response.status === 401 ? 'Please log in again.' :
      response.status === 403 ? 'Access denied or session token expired. Refresh your session and retry.' :
      'The request failed. Please try again.';
    throw new ApiError(data && typeof data.message === 'string' ? data.message : fallback, response.status);
  }
  if (text && data === null) throw new ApiError('The backend returned an unexpected response format.', response.status);
  return data;
}

export async function getSession(signal) {
  const data = await send('/auth/me', 'GET', undefined, signal);
  if (!data || typeof data.csrfToken !== 'string' || !data.csrfToken) {
    throw new ApiError('Authentication endpoint must return user and csrfToken as agreed.', 0);
  }
  csrfToken = data.csrfToken;
  return data.user || null;
}

export function clearToken() { csrfToken = null; }

export async function api(path, method = 'GET', body, signal) {
  if (method !== 'GET' && !csrfToken) await getSession(signal);
  const data = await send(path, method, body, signal);
  if (path === '/auth/login') {
    if (!data || !data.user || !data.csrfToken) throw new ApiError('Login must return user and csrfToken.', 0);
    csrfToken = data.csrfToken;
  }
  if (path === '/auth/logout') clearToken();
  return data;
}
