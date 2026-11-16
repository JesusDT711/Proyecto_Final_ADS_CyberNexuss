// Cliente HTTP: el backend usa HTTP Basic, así que se envía Authorization en cada petición.
let token = null;
export const setAuth = (t) => { token = t; };
export const encode = (u, p) => btoa(unescape(encodeURIComponent(`${u}:${p}`)));
const FRIENDLY = { 400: 'Revisa los datos ingresados.', 403: 'Tu rol no tiene permiso para esta acción.', 404: 'No se encontró el registro.', 409: 'Ya existe un registro con esos datos.', 500: 'No se pudo completar la operación. Verifica que el registro no esté en uso.' };
export async function api(path, { method = 'GET', body, blob } = {}) {
  let r;
  try {
    r = await fetch('/api' + path, {
      method,
      headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest', ...(token && { Authorization: 'Basic ' + token }) },
      body: body && JSON.stringify(body)
    });
  } catch { throw new Error('No hay conexión con el servidor. ¿Está encendido el backend (puerto 8080)?'); }
  if (!r.ok) {
    const t = await r.text(); let m = '';
    try { const j = JSON.parse(t); m = j.message || ''; } catch { m = t; }
    const e = new Error(m && m.length < 200 ? m : FRIENDLY[r.status] || 'Error ' + r.status); e.status = r.status; throw e;
  }
  if (blob) return r.blob();
  return r.status === 204 ? null : r.json().catch(() => null);
}
