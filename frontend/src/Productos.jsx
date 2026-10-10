import { useEffect, useState } from 'react'
import { api, money } from './api.js'

const VACIO = { nombre: '', descripcion: '', precio: '' }

export default function Productos() {
  const [lista, setLista] = useState([])
  const [form, setForm] = useState(VACIO)
  const [editId, setEditId] = useState(null)
  const [error, setError] = useState('')

  const cargar = () => api.productos().then(setLista).catch((e) => setError(e.message))
  useEffect(() => { cargar() }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const guardar = async (e) => {
    e.preventDefault()
    setError('')
    const body = { ...form, precio: parseFloat(form.precio) }
    try {
      if (editId) await api.editarProducto(editId, body)
      else await api.crearProducto(body)
      setForm(VACIO); setEditId(null); cargar()
    } catch (err) { setError(err.message) }
  }

  const editar = (p) => {
    setEditId(p.id)
    setForm({ nombre: p.nombre, descripcion: p.descripcion || '', precio: p.precio })
  }

  const alternar = async (id) => {
    try { await api.alternarProducto(id); cargar() } catch (err) { setError(err.message) }
  }

  return (
    <section>
      <h2>Productos y servicios</h2>
      <form className="form-fila" onSubmit={guardar}>
        <input placeholder="Nombre" value={form.nombre} onChange={set('nombre')} required />
        <input placeholder="Descripción" value={form.descripcion} onChange={set('descripcion')} />
        <input placeholder="Precio" type="number" step="0.01" min="0" value={form.precio} onChange={set('precio')} required />
        <button className="primario">{editId ? 'Guardar cambios' : 'Registrar'}</button>
        {editId && <button type="button" onClick={() => { setEditId(null); setForm(VACIO) }}>Cancelar</button>}
      </form>
      {error && <p className="error">{error}</p>}
      <table>
        <thead><tr><th>Nombre</th><th>Descripción</th><th>Precio</th><th>Estado</th><th></th></tr></thead>
        <tbody>
          {lista.map((p) => (
            <tr key={p.id} className={p.activo ? '' : 'inactivo'}>
              <td>{p.nombre}</td>
              <td>{p.descripcion}</td>
              <td className="num">{money(p.precio)}</td>
              <td>{p.activo ? 'Activo' : 'Inactivo'}</td>
              <td>
                <button onClick={() => editar(p)}>Editar</button>{' '}
                <button onClick={() => alternar(p.id)}>{p.activo ? 'Desactivar' : 'Activar'}</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}
