import { createContext, useContext, useEffect, useState } from 'react';
import { api } from './api';

export const Ctx = createContext();
export const useApp = () => useContext(Ctx);
export const money = (n) => 'S/ ' + Number(n || 0).toLocaleString('es-PE', { minimumFractionDigits: 2 });
export const fdate = (d) => (d ? new Date(d).toLocaleString('es-PE', { dateStyle: 'short', timeStyle: 'short' }) : '—');

export function Modal({ title, onClose, children, wide }) {
  return (
    <div className="modal d-block" style={{ background: '#1b243099' }} onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <div className={'modal-dialog modal-dialog-scrollable' + (wide ? ' modal-lg' : '')}>
        <div className="modal-content">
          <div className="modal-header"><h5 className="modal-title">{title}</h5><button className="btn-close" onClick={onClose} /></div>
          {children}
        </div>
      </div>
    </div>
  );
}

export const Badge = ({ v }) => {
  const c = { PENDIENTE: 'warning', CONFIRMADO: 'success', ENTRADA: 'success', SALIDA: 'secondary', RECIBIDA: 'success', EN_CAMINO: 'info', ENTREGADA: 'success', CANCELADA: 'danger', ACTIVO: 'success', INACTIVO: 'secondary' }[v] || 'secondary';
  return <span className={`badge text-bg-${c}`}>{v}</span>;
};

export function Stock({ p }) {
  const low = p.stock <= p.stockMinimo;
  const pct = Math.min(100, (p.stock / Math.max(1, p.stockMinimo * 3)) * 100);
  return (
    <div className="d-flex align-items-center gap-2">
      <b className={low ? 'text-danger' : ''}>{p.stock}</b>
      <div className="meter flex-grow-1" title={`Mínimo: ${p.stockMinimo}`}><i className={low ? 'low' : ''} style={{ width: pct + '%' }} /></div>
    </div>
  );
}

export function useLoad(path) {
  const { notify } = useApp();
  const [d, setD] = useState(null);
  const load = () => api(path).then(setD).catch((e) => notify(e.message, 'danger'));
  useEffect(() => { load(); }, [path]);
  return [d, load];
}

export const Empty = ({ icon, text }) => (
  <div className="text-center text-secondary py-5"><i className={`bi ${icon} fs-1`} /><p className="mt-2 mb-0">{text}</p></div>
);

// Pantalla CRUD genérica: tabla con buscador + formulario en ventana (patrón típico de paneles tipo AdminLTE/Tabler).
export function Crud({ title, icon, path, cols, fields, searchBy, noun }) {
  const { notify } = useApp();
  const [rows, load] = useLoad(path);
  const [q, setQ] = useState('');
  const [f, setF] = useState(null);
  const list = (rows || []).filter((r) => searchBy.some((k) => String(r[k] ?? '').toLowerCase().includes(q.toLowerCase())));
  const save = async (e) => {
    e.preventDefault();
    const body = { ...f };
    fields.filter((x) => x.t === 'number').forEach((x) => { body[x.k] = Number(f[x.k]); });
    try {
      await api(path + (f.id ? '/' + f.id : ''), { method: f.id ? 'PUT' : 'POST', body });
      notify(f.id ? 'Cambios guardados' : `${noun} registrado`); setF(null); load();
    } catch (x) { notify(x.message, 'danger'); }
  };
  const del = async (r) => {
    if (!confirm('¿Eliminar este registro?')) return;
    try { await api(`${path}/${r.id}`, { method: 'DELETE' }); notify('Registro eliminado'); load(); } catch (x) { notify(x.message, 'danger'); }
  };
  const blank = Object.fromEntries(fields.map((x) => [x.k, x.t === 'checkbox' ? true : x.t === 'number' ? 0 : '']));
  return (
    <div className="page">
      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <h2 className="m-0"><i className={`bi ${icon} me-2`} />{title}</h2>
        <div className="d-flex gap-2">
          <input className="form-control" placeholder="Buscar…" value={q} onChange={(e) => setQ(e.target.value)} />
          <button className="btn btn-primary text-nowrap" onClick={() => setF(blank)}><i className="bi bi-plus-lg" /> Nuevo</button>
        </div>
      </div>
      <div className="panel table-responsive">
        <table className="table table-hover align-middle m-0">
          <thead><tr>{cols.map((c) => <th key={c.l}>{c.l}</th>)}<th /></tr></thead>
          <tbody>
            {list.map((r) => (
              <tr key={r.id}>
                {cols.map((c) => <td key={c.l}>{c.r ? c.r(r) : r[c.k] ?? '—'}</td>)}
                <td className="text-end text-nowrap">
                  <button className="btn btn-sm btn-outline-secondary me-1" title="Editar" onClick={() => setF({ ...r })}><i className="bi bi-pencil" /></button>
                  <button className="btn btn-sm btn-outline-danger" title="Eliminar" onClick={() => del(r)}><i className="bi bi-trash" /></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {rows && !list.length && <Empty icon="bi-inbox" text={`Aún no hay ${title.toLowerCase()} que mostrar.`} />}
      </div>
      {f && (
        <Modal title={f.id ? `Editar ${noun.toLowerCase()}` : `Nuevo ${noun.toLowerCase()}`} onClose={() => setF(null)}>
          <form onSubmit={save}>
            <div className="modal-body row g-3">
              {fields.map((x) => (
                <div key={x.k} className={x.t === 'textarea' || x.w === 12 ? 'col-12' : 'col-md-6'}>
                  {x.t === 'checkbox' ? (
                    <div className="form-check mt-4"><input id={x.k} type="checkbox" className="form-check-input" checked={!!f[x.k]} onChange={(e) => setF({ ...f, [x.k]: e.target.checked })} /><label htmlFor={x.k} className="form-check-label">{x.l}</label></div>
                  ) : (
                    <>
                      <label className="form-label">{x.l}</label>
                      {x.t === 'textarea'
                        ? <textarea className="form-control" rows="2" value={f[x.k] ?? ''} onChange={(e) => setF({ ...f, [x.k]: e.target.value })} />
                        : <input className="form-control" type={x.t || 'text'} step={x.t === 'number' ? '0.01' : undefined} min={x.t === 'number' ? 0 : undefined} required={x.req} value={f[x.k] ?? ''} onChange={(e) => setF({ ...f, [x.k]: e.target.value })} />}
                    </>
                  )}
                </div>
              ))}
            </div>
            <div className="modal-footer"><button type="button" className="btn btn-light" onClick={() => setF(null)}>Cancelar</button><button className="btn btn-primary">Guardar</button></div>
          </form>
        </Modal>
      )}
    </div>
  );
}
