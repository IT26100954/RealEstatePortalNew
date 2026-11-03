import { useEffect, useState } from 'react';
export function navigate(path) { window.location.hash = path; }
export function usePath() {
  function read() { return window.location.hash.slice(1) || '/'; }
  const [path, setPath] = useState(read);
  useEffect(function () {
    function update() { setPath(read()); }
    window.addEventListener('hashchange', update);
    return function () { window.removeEventListener('hashchange', update); };
  }, []);
  return path;
}
