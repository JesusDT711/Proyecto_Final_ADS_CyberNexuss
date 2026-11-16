import { useState } from 'react';
import { Routes, Route, NavLink, Navigate } from 'react-router-dom';
import { api, setAuth, encode } from './api';
import { Ctx, useApp, Crud, Stock, Badge, money } from './ui';
import { Entregas, Abastecimiento, Usuarios } from './pages2';
import { Dashboard, Pedidos, Inventario, Alertas, Reportes } from './pages';

const saved = JSON.parse(sessionStorage.getItem('sumac') || 'null');
if (saved) setAuth(saved.token);

function Login({ onLogin }) {
  const [c, setC] = useState(''); const [p, setP] = useState(''); const [err, setErr] = useState('');
  const go = async (e) => {
    e.preventDefault();
    if (!c || !p) return setErr('Completa tu correo y contraseña.');
    const token = encode(c, p); setAuth(token);
    try {
      const us = await api('/usuarios'); // solo el ADMIN puede listar usuarios
      const me = us.find((u) => u.correo === c);
      onLogin(token, { id: me?.id, nombre: me?.nombre || c, correo: c, admin: true });
    } catch (x) {
      if (x.status === 403) onLogin(token, { nombre: c, correo: c, admin: false }); // credenciales válidas, rol EMPLEADO
      else { setAuth(null); setErr(x.status === 401 ? 'Correo o contraseña incorrectos.' : x.message); }
    }
  };
  return (
    <div className="login"><form className="panel" onSubmit={go}>
      <h3 className="brand mb-1"><i className="bi bi-box-seam text-warning" /> Sumac Yanahuara</h3>
      <p className="text-secondary">Ventas, inventario y distribución</p>
      <label className="form-label">Correo</label><input className="form-control mb-3" type="email" autoFocus value={c} onChange={(e) => setC(e.target.value)} />
      <label className="form-label">Contraseña</label><input className="form-control mb-3" type="password" value={p} onChange={(e) => setP(e.target.value)} />
      {err && <div className="alert alert-danger py-2">{err}</div>}
      <button className="btn btn-primary w-100">Ingresar</button>
    </form></div>
  );
}

const NAV = [['/', 'bi-speedometer2', 'Inicio'], ['/pedidos', 'bi-receipt', 'Pedidos'], ['/entregas', 'bi-geo-alt', 'Entregas'], ['/clientes', 'bi-people', 'Clientes'], ['/productos', 'bi-box-seam', 'Productos'], ['/inventario', 'bi-boxes', 'Inventario'], ['/alertas', 'bi-bell', 'Alertas'], ['/reportes', 'bi-file-earmark-pdf', 'Reportes']];

export default function App() {
  const [user, setUser] = useState(saved?.user || null);
  const [toast, setToast] = useState(null);
  const [open, setOpen] = useState(false);
  const notify = (m, t = 'success') => { setToast({ m, t }); setTimeout(() => setToast(null), 4500); };
  const login = (token, u) => { sessionStorage.setItem('sumac', JSON.stringify({ token, user: u })); setUser(u); };
  const logout = () => { sessionStorage.removeItem('sumac'); setAuth(null); setUser(null); };
  if (!user) return <Ctx.Provider value={{ notify }}><Login onLogin={login} /></Ctx.Provider>;
  const admin = (el) => (user.admin ? el : <Navigate to="/" />);
  return (
    <Ctx.Provider value={{ user, notify }}>
      <aside className={'side' + (open ? ' open' : '')} onClick={() => setOpen(false)}>
        <div className="brand"><i className="bi bi-box-seam" /> Sumac</div>
        {[...NAV, ...(user.admin ? [['/proveedores', 'bi-shop', 'Proveedores'], ['/abastecimiento', 'bi-truck', 'Abastecimiento'], ['/usuarios', 'bi-person-gear', 'Usuarios']] : [])].map(([to, ic, l]) => (
          <NavLink key={to} to={to} end={to === '/'}><i className={`bi ${ic}`} />{l}</NavLink>
        ))}
      </aside>
      <div className="main">
        <header className="top">
          <button className="btn btn-light menu-btn" onClick={() => setOpen(!open)}><i className="bi bi-list" /></button>
          <span className="ms-auto me-3 small">{user.nombre} <span className="badge text-bg-secondary">{user.admin ? 'Administrador' : 'Empleado'}</span></span>
          <button className="btn btn-sm btn-outline-secondary" onClick={logout}>Salir</button>
        </header>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/pedidos" element={<Pedidos />} />
          <Route path="/entregas" element={<Entregas />} />
          <Route path="/abastecimiento" element={admin(<Abastecimiento />)} />
          <Route path="/usuarios" element={admin(<Usuarios />)} />
          <Route path="/inventario" element={<Inventario />} />
          <Route path="/alertas" element={<Alertas full />} />
          <Route path="/reportes" element={<Reportes />} />
          <Route path="/clientes" element={<Crud title="Clientes" noun="Cliente" icon="bi-people" path="/clientes" searchBy={['nombre', 'documento', 'correo']}
            cols={[{ l: 'Nombre', k: 'nombre' }, { l: 'DNI / RUC', k: 'documento' }, { l: 'Teléfono', k: 'telefono' }, { l: 'Correo', k: 'correo' }]}
            fields={[{ k: 'nombre', l: 'Nombre o razón social', req: 1 }, { k: 'documento', l: 'DNI / RUC' }, { k: 'telefono', l: 'Teléfono', t: 'tel' }, { k: 'correo', l: 'Correo', t: 'email' }, { k: 'direccion', l: 'Dirección', w: 12 }]} />} />
          <Route path="/productos" element={<Crud title="Productos" noun="Producto" icon="bi-box-seam" path="/productos" searchBy={['nombre', 'codigo', 'categoria']}
            cols={[{ l: 'Código', k: 'codigo' }, { l: 'Producto', k: 'nombre' }, { l: 'Categoría', k: 'categoria' }, { l: 'Precio', r: (r) => money(r.precio) }, { l: 'Stock', r: (r) => <Stock p={r} /> }, { l: 'Estado', r: (r) => <Badge v={r.activo ? 'ACTIVO' : 'INACTIVO'} /> }]}
            fields={[{ k: 'codigo', l: 'Código', req: 1 }, { k: 'nombre', l: 'Nombre', req: 1 }, { k: 'categoria', l: 'Categoría (ej. Ferretería, Fontanería)', req: 1 }, { k: 'precio', l: 'Precio (S/)', t: 'number', req: 1 }, { k: 'stock', l: 'Stock inicial', t: 'number' }, { k: 'stockMinimo', l: 'Stock mínimo', t: 'number' }, { k: 'descripcion', l: 'Descripción', t: 'textarea' }, { k: 'activo', l: 'Producto activo', t: 'checkbox' }]} />} />
          <Route path="/proveedores" element={admin(<Crud title="Proveedores" noun="Proveedor" icon="bi-shop" path="/proveedores" searchBy={['nombre', 'ruc']}
            cols={[{ l: 'Nombre', k: 'nombre' }, { l: 'RUC', k: 'ruc' }, { l: 'Teléfono', k: 'telefono' }, { l: 'Correo', k: 'correo' }]}
            fields={[{ k: 'nombre', l: 'Razón social', req: 1 }, { k: 'ruc', l: 'RUC' }, { k: 'telefono', l: 'Teléfono', t: 'tel' }, { k: 'correo', l: 'Correo', t: 'email' }, { k: 'direccion', l: 'Dirección', w: 12 }, { k: 'activo', l: 'Proveedor activo', t: 'checkbox' }]} />)} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
      {toast && <div className="toast-box"><div className={`alert alert-${toast.t} shadow mb-0`}>{toast.m}</div></div>}
    </Ctx.Provider>
  );
}
