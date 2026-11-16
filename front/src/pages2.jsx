import { useState } from 'react';
import { api } from './api';
import { useApp, useLoad, Modal, Badge, Empty, money, fdate } from './ui';

const ESTADOS = ['PENDIENTE', 'EN_CAMINO', 'ENTREGADA', 'CANCELADA'];

export function Entregas() {
  const { notify } = useApp();
  const [rows, load] = useLoad('/entregas');
  const [peds] = useLoad('/pedidos');
  const [f, setF] = useState(null);
  const libres = (peds || []).filter((p) => p.estado === 'CONFIRMADO' && !(rows || []).some((e) => e.pedido?.id === p.id));
  const save = async (e) => {
    e.preventDefault();
    const body = { ...f, pedido: { id: f.id ? f.pedido.id : +f.pid }, fechaProgramada: f.fechaProgramada || null };
    try {
      await api('/entregas' + (f.id ? '/' + f.id : ''), { method: f.id ? 'PUT' : 'POST', body });
      notify(f.id ? 'Entrega actualizada' : 'Entrega programada'); setF(null); load();
    } catch (x) { notify(x.message, 'danger'); }
  };
  const del = async (r) => {
    if (!confirm('¿Eliminar esta entrega?')) return;
    try { await api('/entregas/' + r.id, { method: 'DELETE' }); notify('Entrega eliminada'); load(); } catch (x) { notify(x.message, 'danger'); }
  };
  const pick = (id) => setF({ ...f, pid: id, direccionEntrega: peds.find((p) => p.id === +id)?.cliente?.direccion || f.direccionEntrega });
  const cerrada = (r) => r.estado === 'ENTREGADA' || r.estado === 'CANCELADA';
  return (
    <div className="page">
      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <h2 className="m-0"><i className="bi bi-geo-alt me-2" />Entregas</h2>
        <button className="btn btn-primary" onClick={() => setF({ pid: '', direccionEntrega: '', fechaProgramada: '', responsable: '', observaciones: '', estado: 'PENDIENTE' })}><i className="bi bi-plus-lg" /> Programar entrega</button>
      </div>
      <div className="panel table-responsive">
        <table className="table table-hover align-middle m-0">
          <thead><tr><th>Pedido</th><th>Cliente</th><th>Dirección</th><th>Programada</th><th>Responsable</th><th>Estado</th><th /></tr></thead>
          <tbody>{(rows || []).sort((a, b) => b.id - a.id).map((r) => (
            <tr key={r.id}><td>#{r.pedido?.id}</td><td>{r.pedido?.cliente?.nombre}</td><td>{r.direccionEntrega}</td><td>{fdate(r.fechaProgramada)}</td><td>{r.responsable || '—'}</td><td><Badge v={r.estado} /></td>
              <td className="text-end text-nowrap">
                {!cerrada(r) && <button className="btn btn-sm btn-outline-secondary me-1" title="Actualizar estado" onClick={() => setF({ ...r })}><i className="bi bi-pencil" /></button>}
                {r.estado !== 'ENTREGADA' && <button className="btn btn-sm btn-outline-danger" onClick={() => del(r)}><i className="bi bi-trash" /></button>}
              </td></tr>
          ))}</tbody>
        </table>
        {rows && !rows.length && <Empty icon="bi-geo-alt" text="Aún no hay entregas. Solo se pueden programar para pedidos confirmados." />}
      </div>
      {f && (
        <Modal title={f.id ? `Entrega del pedido #${f.pedido.id}` : 'Programar entrega'} onClose={() => setF(null)}>
          <form onSubmit={save}>
            <div className="modal-body row g-3">
              {!f.id && <div className="col-12"><label className="form-label">Pedido confirmado</label>
                <select className="form-select" required value={f.pid} onChange={(e) => pick(e.target.value)}><option value="">Selecciona…</option>{libres.map((p) => <option key={p.id} value={p.id}>#{p.id} · {p.cliente?.nombre} · {money(p.total)}</option>)}</select>
                {peds && !libres.length && <div className="form-text">No hay pedidos confirmados sin entrega.</div>}</div>}
              <div className="col-12"><label className="form-label">Dirección de entrega</label><input className="form-control" required value={f.direccionEntrega} onChange={(e) => setF({ ...f, direccionEntrega: e.target.value })} /></div>
              <div className="col-md-6"><label className="form-label">Fecha programada</label><input type="datetime-local" className="form-control" value={(f.fechaProgramada || '').slice(0, 16)} onChange={(e) => setF({ ...f, fechaProgramada: e.target.value })} /></div>
              <div className="col-md-6"><label className="form-label">Responsable</label><input className="form-control" value={f.responsable || ''} onChange={(e) => setF({ ...f, responsable: e.target.value })} /></div>
              {f.id && <div className="col-12"><label className="form-label">Estado</label><select className="form-select" value={f.estado} onChange={(e) => setF({ ...f, estado: e.target.value })}>{ESTADOS.map((s) => <option key={s}>{s}</option>)}</select></div>}
              <div className="col-12"><label className="form-label">Observaciones</label><input className="form-control" value={f.observaciones || ''} onChange={(e) => setF({ ...f, observaciones: e.target.value })} /></div>
            </div>
            <div className="modal-footer"><button type="button" className="btn btn-light" onClick={() => setF(null)}>Cancelar</button><button className="btn btn-primary">Guardar</button></div>
          </form>
        </Modal>
      )}
    </div>
  );
}

export function Abastecimiento() {
  const { notify, user } = useApp();
  const [rows, load] = useLoad('/solicitudes-abastecimiento');
  const [provs] = useLoad('/proveedores/activos');
  const [prods] = useLoad('/productos/activos');
  const [f, setF] = useState(null);
  const [det, setDet] = useState(null);
  const ver = async (s) => { try { setDet({ s, d: await api('/detalles-abastecimiento/solicitud/' + s.id) }); } catch (e) { notify(e.message, 'danger'); } };
  const submit = async (e) => {
    e.preventDefault();
    try {
      const s = await api('/solicitudes-abastecimiento', { method: 'POST', body: { proveedor: { id: +f.prov }, estado: 'PENDIENTE', observaciones: f.obs, ...(user.id && { usuario: { id: user.id } }) } });
      for (const l of f.lines) await api('/detalles-abastecimiento', { method: 'POST', body: { solicitud: { id: s.id }, producto: { id: +l.pid }, cantidad: +l.qty, costoUnitario: Number(l.costo) } });
      notify(`Solicitud #${s.id} registrada`); setF(null); load();
    } catch (x) { notify(x.message, 'danger'); }
  };
  const recibir = async (s) => {
    try { await api(`/solicitudes-abastecimiento/${s.id}/recibir`, { method: 'POST' }); notify(`Solicitud #${s.id} recibida: stock actualizado`); setDet(null); load(); } catch (x) { notify(x.message, 'danger'); }
  };
  const setL = (i, p) => setF({ ...f, lines: f.lines.map((l, j) => (j === i ? { ...l, ...p } : l)) });
  return (
    <div className="page">
      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <h2 className="m-0"><i className="bi bi-truck me-2" />Abastecimiento</h2>
        <button className="btn btn-primary" onClick={() => setF({ prov: '', obs: '', lines: [{ pid: '', qty: 1, costo: '' }] })}><i className="bi bi-plus-lg" /> Nueva solicitud</button>
      </div>
      <div className="panel table-responsive">
        <table className="table table-hover align-middle m-0">
          <thead><tr><th>N.º</th><th>Proveedor</th><th>Fecha</th><th>Estado</th><th /></tr></thead>
          <tbody>{(rows || []).sort((a, b) => b.id - a.id).map((s) => (
            <tr key={s.id}><td>#{s.id}</td><td>{s.proveedor?.nombre}</td><td>{fdate(s.fechaSolicitud)}</td><td><Badge v={s.estado} /></td><td className="text-end"><button className="btn btn-sm btn-outline-secondary" onClick={() => ver(s)}>Ver detalle</button></td></tr>
          ))}</tbody>
        </table>
        {rows && !rows.length && <Empty icon="bi-truck" text="Aún no hay solicitudes a proveedores." />}
      </div>
      {f && (
        <Modal wide title="Nueva solicitud de abastecimiento" onClose={() => setF(null)}>
          <form onSubmit={submit}>
            <div className="modal-body">
              <select className="form-select mb-3" required value={f.prov} onChange={(e) => setF({ ...f, prov: e.target.value })}><option value="">Proveedor…</option>{(provs || []).map((p) => <option key={p.id} value={p.id}>{p.nombre}</option>)}</select>
              {f.lines.map((l, i) => (
                <div key={i} className="row g-2 mb-2">
                  <div className="col-6"><select className="form-select" required value={l.pid} onChange={(e) => setL(i, { pid: e.target.value })}><option value="">Producto…</option>{(prods || []).map((p) => <option key={p.id} value={p.id}>{p.nombre} (stock {p.stock})</option>)}</select></div>
                  <div className="col-2"><input type="number" min="1" className="form-control" placeholder="Cant." required value={l.qty} onChange={(e) => setL(i, { qty: e.target.value })} /></div>
                  <div className="col-3"><input type="number" min="0" step="0.01" className="form-control" placeholder="Costo unit." required value={l.costo} onChange={(e) => setL(i, { costo: e.target.value })} /></div>
                  <div className="col-1"><button type="button" className="btn btn-outline-danger" disabled={f.lines.length < 2} onClick={() => setF({ ...f, lines: f.lines.filter((_, j) => j !== i) })}><i className="bi bi-x" /></button></div>
                </div>
              ))}
              <button type="button" className="btn btn-sm btn-outline-primary" onClick={() => setF({ ...f, lines: [...f.lines, { pid: '', qty: 1, costo: '' }] })}><i className="bi bi-plus" /> Agregar producto</button>
              <input className="form-control mt-3" placeholder="Observaciones (opcional)" value={f.obs} onChange={(e) => setF({ ...f, obs: e.target.value })} />
            </div>
            <div className="modal-footer"><b className="me-auto">Total: {money(f.lines.reduce((s, l) => s + l.qty * l.costo, 0))}</b><button type="button" className="btn btn-light" onClick={() => setF(null)}>Cancelar</button><button className="btn btn-primary">Registrar solicitud</button></div>
          </form>
        </Modal>
      )}
      {det && (
        <Modal title={`Solicitud #${det.s.id} · ${det.s.proveedor?.nombre}`} onClose={() => setDet(null)}>
          <div className="modal-body">
            <table className="table table-sm"><thead><tr><th>Producto</th><th>Cant.</th><th className="text-end">Costo unit.</th></tr></thead>
              <tbody>{det.d.map((x) => <tr key={x.id}><td>{x.producto?.nombre}</td><td>{x.cantidad}</td><td className="text-end">{money(x.costoUnitario)}</td></tr>)}</tbody></table>
            <div className="text-end"><b>Total {money(det.d.reduce((s, x) => s + x.cantidad * x.costoUnitario, 0))}</b></div>
          </div>
          <div className="modal-footer"><Badge v={det.s.estado} />{det.s.estado === 'PENDIENTE' && <button className="btn btn-success" onClick={() => recibir(det.s)}><i className="bi bi-box-arrow-in-down" /> Recibir mercadería</button>}</div>
        </Modal>
      )}
    </div>
  );
}

export function Usuarios() {
  const { notify } = useApp();
  const [rows, load] = useLoad('/usuarios');
  const [roles] = useLoad('/roles');
  const [f, setF] = useState(null);
  const save = async (e) => {
    e.preventDefault();
    try { await api('/usuarios', { method: 'POST', body: { ...f, rolId: +f.rolId } }); notify('Usuario creado'); setF(null); load(); } catch (x) { notify(x.message, 'danger'); }
  };
  const nom = (r) => (r || '').replace('ROLE_', '').toLowerCase();
  return (
    <div className="page">
      <div className="d-flex justify-content-between mb-3">
        <h2 className="m-0"><i className="bi bi-person-gear me-2" />Usuarios</h2>
        <button className="btn btn-primary" onClick={() => setF({ nombre: '', correo: '', contrasena: '', rolId: '' })}><i className="bi bi-plus-lg" /> Nuevo usuario</button>
      </div>
      <div className="panel table-responsive">
        <table className="table align-middle m-0"><thead><tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Estado</th></tr></thead>
          <tbody>{(rows || []).map((u) => <tr key={u.id}><td>{u.nombre}</td><td>{u.correo}</td><td className="text-capitalize">{nom(u.rol)}</td><td><Badge v={u.activo ? 'ACTIVO' : 'INACTIVO'} /></td></tr>)}</tbody></table>
      </div>
      {f && (
        <Modal title="Nuevo usuario" onClose={() => setF(null)}>
          <form onSubmit={save}>
            <div className="modal-body row g-3">
              <div className="col-12"><label className="form-label">Nombre</label><input className="form-control" required value={f.nombre} onChange={(e) => setF({ ...f, nombre: e.target.value })} /></div>
              <div className="col-12"><label className="form-label">Correo</label><input type="email" className="form-control" required value={f.correo} onChange={(e) => setF({ ...f, correo: e.target.value })} /></div>
              <div className="col-md-6"><label className="form-label">Contraseña (mín. 8)</label><input type="password" minLength={8} className="form-control" required value={f.contrasena} onChange={(e) => setF({ ...f, contrasena: e.target.value })} /></div>
              <div className="col-md-6"><label className="form-label">Rol</label><select className="form-select" required value={f.rolId} onChange={(e) => setF({ ...f, rolId: e.target.value })}><option value="">Selecciona…</option>{(roles || []).map((r) => <option key={r.id} value={r.id}>{nom(r.nombre)}</option>)}</select></div>
            </div>
            <div className="modal-footer"><button type="button" className="btn btn-light" onClick={() => setF(null)}>Cancelar</button><button className="btn btn-primary">Crear usuario</button></div>
          </form>
        </Modal>
      )}
    </div>
  );
}
