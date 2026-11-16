import { useState } from 'react';
import { api } from './api';
import { useApp, useLoad, Modal, Badge, Stock, Empty, money, fdate } from './ui';

export function Alertas({ full }) {
  const { notify } = useApp();
  const [a, load] = useLoad('/alertas-stock/pendientes');
  const ok = async (x) => {
    try { await api(`/alertas-stock/${x.id}/atender`, { method: 'PATCH' }); notify('Alerta atendida'); load(); } catch (e) { notify(e.message, 'danger'); }
  };
  return (
    <div className={full ? 'page' : ''}>
      {full && <h2 className="mb-3"><i className="bi bi-bell me-2" />Alertas de stock</h2>}
      <div className="panel">
        {(a || []).map((x) => (
          <div key={x.id} className="d-flex justify-content-between align-items-center gap-3 p-3 border-bottom">
            <div><b>{x.producto?.nombre}</b><div className="small text-secondary">{x.mensaje}</div></div>
            <button className="btn btn-sm btn-outline-success text-nowrap" onClick={() => ok(x)}>Marcar atendida</button>
          </div>
        ))}
        {a && !a.length && <Empty icon="bi-check2-circle" text="Todo el inventario está sobre su stock mínimo." />}
      </div>
      {full && <p className="small text-secondary mt-2">Para atender una alerta, primero repón el producto con una entrada en Inventario.</p>}
    </div>
  );
}

export function Dashboard() {
  const { user } = useApp();
  const [prods] = useLoad('/productos/activos');
  const [peds] = useLoad('/pedidos');
  const [al] = useLoad('/alertas-stock/pendientes');
  const pend = (peds || []).filter((p) => p.estado === 'PENDIENTE').length;
  const ventas = (peds || []).filter((p) => p.estado === 'CONFIRMADO').reduce((s, p) => s + Number(p.total), 0);
  const K = ({ l, v, warn }) => <div className="col-6 col-lg-3"><div className={'panel kpi ' + (warn ? 'warn' : '')}><span className="text-secondary small">{l}</span><b>{v}</b></div></div>;
  return (
    <div className="page">
      <h2>Hola, {user.nombre}</h2>
      <p className="text-secondary">Resumen de hoy en Inversiones Sumac Yanahuara.</p>
      <div className="row g-3 mb-4">
        <K l="Productos activos" v={prods?.length ?? '…'} />
        <K l="Alertas de stock bajo" v={al?.length ?? '…'} warn={al?.length > 0} />
        <K l="Pedidos pendientes" v={peds ? pend : '…'} />
        <K l="Ventas confirmadas" v={peds ? money(ventas) : '…'} />
      </div>
      <h4>Stock bajo</h4>
      <Alertas />
    </div>
  );
}

function NuevoPedido({ onClose, onDone }) {
  const { notify } = useApp();
  const [clientes] = useLoad('/clientes');
  const [prods] = useLoad('/productos/activos');
  const [cli, setCli] = useState('');
  const [obs, setObs] = useState('');
  const [lines, setLines] = useState([{ pid: '', qty: 1 }]);
  const [busy, setBusy] = useState(false);
  const prod = (id) => (prods || []).find((p) => p.id === +id);
  const total = lines.reduce((s, l) => s + (prod(l.pid)?.precio || 0) * l.qty, 0);
  const bad = lines.some((l) => !l.pid || l.qty < 1 || l.qty > (prod(l.pid)?.stock ?? 0));
  const set = (i, patch) => setLines(lines.map((l, j) => (j === i ? { ...l, ...patch } : l)));
  const submit = async (e) => {
    e.preventDefault(); setBusy(true);
    try {
      const p = await api('/pedidos', { method: 'POST', body: { cliente: { id: +cli }, estado: 'PENDIENTE', observaciones: obs } });
      for (const l of lines) await api('/detalles-pedido', { method: 'POST', body: { pedido: { id: p.id }, producto: { id: +l.pid }, cantidad: +l.qty } });
      notify(`Pedido #${p.id} registrado como pendiente`); onDone();
    } catch (x) { notify(x.message, 'danger'); setBusy(false); }
  };
  return (
    <Modal wide title="Nuevo pedido" onClose={onClose}>
      <form onSubmit={submit}>
        <div className="modal-body">
          <label className="form-label">Cliente</label>
          <select className="form-select mb-3" required value={cli} onChange={(e) => setCli(e.target.value)}>
            <option value="">Selecciona un cliente…</option>
            {(clientes || []).map((c) => <option key={c.id} value={c.id}>{c.nombre} {c.documento ? `· ${c.documento}` : ''}</option>)}
          </select>
          {lines.map((l, i) => {
            const p = prod(l.pid);
            return (
              <div key={i} className="row g-2 mb-2 align-items-center">
                <div className="col-7"><select className="form-select" value={l.pid} onChange={(e) => set(i, { pid: e.target.value })}><option value="">Producto…</option>{(prods || []).map((x) => <option key={x.id} value={x.id}>{x.nombre} (stock {x.stock})</option>)}</select></div>
                <div className="col-2"><input type="number" min="1" className="form-control" value={l.qty} onChange={(e) => set(i, { qty: +e.target.value })} /></div>
                <div className="col-2 text-end small">{p ? money(p.precio * l.qty) : ''}{p && l.qty > p.stock && <div className="text-danger">Solo hay {p.stock}</div>}</div>
                <div className="col-1"><button type="button" className="btn btn-sm btn-outline-danger" disabled={lines.length < 2} onClick={() => setLines(lines.filter((_, j) => j !== i))}><i className="bi bi-x" /></button></div>
              </div>
            );
          })}
          <button type="button" className="btn btn-sm btn-outline-primary" onClick={() => setLines([...lines, { pid: '', qty: 1 }])}><i className="bi bi-plus" /> Agregar producto</button>
          <textarea className="form-control mt-3" rows="2" placeholder="Observaciones (opcional)" value={obs} onChange={(e) => setObs(e.target.value)} />
        </div>
        <div className="modal-footer justify-content-between"><b className="fs-5">Total: {money(total)}</b><div><button type="button" className="btn btn-light me-2" onClick={onClose}>Cancelar</button><button className="btn btn-primary" disabled={busy || bad || !cli}>Registrar pedido</button></div></div>
      </form>
    </Modal>
  );
}

export function Pedidos() {
  const { notify } = useApp();
  const [rows, load] = useLoad('/pedidos');
  const [est, setEst] = useState('');
  const [nuevo, setNuevo] = useState(false);
  const [det, setDet] = useState(null);
  const list = (rows || []).filter((r) => !est || r.estado === est).sort((a, b) => b.id - a.id);
  const ver = async (p) => { try { setDet({ p, d: await api('/detalles-pedido/pedido/' + p.id) }); } catch (e) { notify(e.message, 'danger'); } };
  const conf = async (p) => {
    try { await api(`/pedidos/${p.id}/confirmar`, { method: 'POST' }); notify(`Pedido #${p.id} confirmado y stock actualizado`); setDet(null); load(); } catch (e) { notify(e.message, 'danger'); }
  };
  return (
    <div className="page">
      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <h2 className="m-0"><i className="bi bi-receipt me-2" />Pedidos y ventas</h2>
        <div className="d-flex gap-2">
          <select className="form-select" value={est} onChange={(e) => setEst(e.target.value)}><option value="">Todos</option><option>PENDIENTE</option><option>CONFIRMADO</option></select>
          <button className="btn btn-primary text-nowrap" onClick={() => setNuevo(true)}><i className="bi bi-plus-lg" /> Nuevo pedido</button>
        </div>
      </div>
      <div className="panel table-responsive">
        <table className="table table-hover align-middle m-0">
          <thead><tr><th>N.º</th><th>Cliente</th><th>Fecha</th><th>Estado</th><th className="text-end">Total</th><th /></tr></thead>
          <tbody>{list.map((p) => (
            <tr key={p.id}><td>#{p.id}</td><td>{p.cliente?.nombre}</td><td>{fdate(p.fechaPedido)}</td><td><Badge v={p.estado} /></td><td className="text-end">{money(p.total)}</td>
              <td className="text-end text-nowrap"><button className="btn btn-sm btn-outline-secondary" onClick={() => ver(p)}>Ver detalle</button></td></tr>
          ))}</tbody>
        </table>
        {rows && !list.length && <Empty icon="bi-receipt" text="No hay pedidos con este filtro." />}
      </div>
      {nuevo && <NuevoPedido onClose={() => setNuevo(false)} onDone={() => { setNuevo(false); load(); }} />}
      {det && (
        <Modal title={`Pedido #${det.p.id} · ${det.p.cliente?.nombre}`} onClose={() => setDet(null)}>
          <div className="modal-body">
            <table className="table table-sm"><thead><tr><th>Producto</th><th>Cant.</th><th className="text-end">P. unit.</th><th className="text-end">Subtotal</th></tr></thead>
              <tbody>{det.d.map((x) => <tr key={x.id}><td>{x.producto?.nombre}</td><td>{x.cantidad}</td><td className="text-end">{money(x.precioUnitario)}</td><td className="text-end">{money(x.subtotal)}</td></tr>)}</tbody></table>
            <div className="text-end fs-5"><b>Total {money(det.p.total)}</b></div>
          </div>
          <div className="modal-footer">
            <Badge v={det.p.estado} />
            {det.p.estado === 'PENDIENTE' && <button className="btn btn-success" onClick={() => conf(det.p)}><i className="bi bi-check2" /> Confirmar venta</button>}
          </div>
        </Modal>
      )}
    </div>
  );
}

export function Inventario() {
  const { notify, user } = useApp();
  const [mov, load] = useLoad('/movimientos-inventario');
  const [prods, loadP] = useLoad('/productos');
  const [f, setF] = useState(null);
  const save = async (e) => {
    e.preventDefault();
    try {
      await api('/movimientos-inventario', { method: 'POST', body: { producto: { id: +f.pid }, tipoMovimiento: f.tipo, cantidad: +f.qty, observacion: f.obs, ...(user.id && { usuario: { id: user.id } }) } });
      notify('Movimiento registrado y stock actualizado'); setF(null); load(); loadP();
    } catch (x) { notify(x.message, 'danger'); }
  };
  return (
    <div className="page">
      <div className="d-flex flex-wrap justify-content-between gap-2 mb-3">
        <h2 className="m-0"><i className="bi bi-boxes me-2" />Inventario</h2>
        <button className="btn btn-primary" onClick={() => setF({ pid: '', tipo: 'ENTRADA', qty: 1, obs: '' })}><i className="bi bi-arrow-left-right" /> Registrar entrada o salida</button>
      </div>
      <div className="row g-3">
        <div className="col-lg-5">
          <div className="panel table-responsive"><table className="table align-middle m-0"><thead><tr><th>Producto</th><th style={{ width: '45%' }}>Stock</th></tr></thead>
            <tbody>{(prods || []).filter((p) => p.activo).map((p) => <tr key={p.id}><td>{p.nombre}<div className="small text-secondary">{p.codigo}</div></td><td><Stock p={p} /></td></tr>)}</tbody></table></div>
        </div>
        <div className="col-lg-7">
          <div className="panel table-responsive"><table className="table align-middle m-0"><thead><tr><th>Fecha</th><th>Producto</th><th>Tipo</th><th>Cant.</th></tr></thead>
            <tbody>{(mov || []).sort((a, b) => b.id - a.id).map((m) => <tr key={m.id}><td>{fdate(m.fechaMovimiento)}</td><td>{m.producto?.nombre}<div className="small text-secondary">{m.observacion}</div></td><td><Badge v={m.tipoMovimiento} /></td><td>{m.cantidad}</td></tr>)}</tbody></table>
            {mov && !mov.length && <Empty icon="bi-clock-history" text="Todavía no hay movimientos." />}</div>
        </div>
      </div>
      {f && (
        <Modal title="Registrar movimiento" onClose={() => setF(null)}>
          <form onSubmit={save}>
            <div className="modal-body row g-3">
              <div className="col-12"><label className="form-label">Producto</label><select className="form-select" required value={f.pid} onChange={(e) => setF({ ...f, pid: e.target.value })}><option value="">Selecciona…</option>{(prods || []).filter((p) => p.activo).map((p) => <option key={p.id} value={p.id}>{p.nombre} (stock {p.stock})</option>)}</select></div>
              <div className="col-6"><label className="form-label">Tipo</label><select className="form-select" value={f.tipo} onChange={(e) => setF({ ...f, tipo: e.target.value })}><option value="ENTRADA">Entrada (ingresa mercadería)</option><option value="SALIDA">Salida (sale mercadería)</option></select></div>
              <div className="col-6"><label className="form-label">Cantidad</label><input type="number" min="1" className="form-control" required value={f.qty} onChange={(e) => setF({ ...f, qty: e.target.value })} /></div>
              <div className="col-12"><label className="form-label">Observación</label><input className="form-control" value={f.obs} onChange={(e) => setF({ ...f, obs: e.target.value })} /></div>
            </div>
            <div className="modal-footer"><button type="button" className="btn btn-light" onClick={() => setF(null)}>Cancelar</button><button className="btn btn-primary">Guardar</button></div>
          </form>
        </Modal>
      )}
    </div>
  );
}

const REPS = [
  ['inventario', 'Inventario general', 'bi-boxes', 'Todos los productos con su stock actual.'],
  ['stock-bajo', 'Stock bajo', 'bi-exclamation-triangle', 'Productos en o bajo su stock mínimo.'],
  ['ventas', 'Ventas', 'bi-graph-up', 'Pedidos y ventas registradas.'],
  ['movimientos', 'Movimientos', 'bi-arrow-left-right', 'Entradas y salidas de mercadería.'],
  ['abastecimiento', 'Abastecimiento', 'bi-truck', 'Solicitudes a proveedores.'],
  ['entregas', 'Entregas', 'bi-geo-alt', 'Estado de las entregas a clientes.']
];
export function Reportes() {
  const { notify } = useApp();
  const [ini, setIni] = useState('');
  const [fin, setFin] = useState('');
  const open = async (r) => {
    const w = window.open('', '_blank');
    const rango = ['ventas', 'movimientos', 'abastecimiento', 'entregas'].includes(r) ? `?${ini ? 'inicio=' + ini : ''}${ini && fin ? '&' : ''}${fin ? 'fin=' + fin : ''}` : '';
    try { const b = await api(`/reportes/${r}/pdf${rango}`, { blob: true }); w.location = URL.createObjectURL(b); }
    catch (e) { w.close(); notify(e.message, 'danger'); }
  };
  return (
    <div className="page">
      <h2 className="mb-1"><i className="bi bi-file-earmark-pdf me-2" />Reportes</h2>
      <p className="text-secondary">Se abren en una pestaña nueva: desde ahí puedes imprimirlos o descargarlos.</p>
      <div className="panel p-3 mb-3 d-flex flex-wrap gap-3 align-items-end">
        <div><label className="form-label mb-1">Desde</label><input type="date" className="form-control" value={ini} onChange={(e) => setIni(e.target.value)} /></div>
        <div><label className="form-label mb-1">Hasta</label><input type="date" className="form-control" value={fin} onChange={(e) => setFin(e.target.value)} /></div>
        <span className="small text-secondary">El rango se aplica a ventas, movimientos, abastecimiento y entregas. Déjalo vacío para incluir todo.</span>
      </div>
      <div className="row g-3">{REPS.map(([k, t, i, d]) => (
        <div key={k} className="col-md-6 col-lg-4"><div className="panel p-3 h-100 d-flex flex-column"><h5><i className={`bi ${i} me-2`} />{t}</h5><p className="text-secondary small flex-grow-1">{d}</p><button className="btn btn-outline-primary" onClick={() => open(k)}>Ver reporte PDF</button></div></div>
      ))}</div>
    </div>
  );
}
