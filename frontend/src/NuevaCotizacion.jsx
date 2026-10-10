import { useEffect, useState } from 'react'
import { api, pdfUrl, money } from './api.js'

const IGV = 0.18
const ITEM = { productoId: '', concepto: '', cantidad: 1, precioUnitario: 0 }

export default function NuevaCotizacion({ onTerminar }) {
  const [productos, setProductos] = useState([])
  const [cliente, setCliente] = useState({ clienteNombre: '', clienteDocumento: '', clienteEmail: '', clienteTelefono: '' })
  const [items, setItems] = useState([{ ...ITEM }])
  const [error, setError] = useState('')
  const [creada, setCreada] = useState(null)

  useEffect(() => { api.productos(true).then(setProductos).catch((e) => setError(e.message)) }, [])

  const setItem = (i, cambios) => setItems(items.map((it, j) => (j === i ? { ...it, ...cambios } : it)))

  const elegirProducto = (i, id) => {
    const p = productos.find((x) => String(x.id) === id)
    if (p) setItem(i, { productoId: p.id, concepto: p.nombre, precioUnitario: p.precio })
    else setItem(i, { productoId: '', concepto: '', precioUnitario: 0 })
  }

  const subtotal = items.reduce((s, it) => s + Number(it.cantidad || 0) * Number(it.precioUnitario || 0), 0)
  const igv = Math.round(subtotal * IGV * 100) / 100
  const total = subtotal + igv

  const guardar = async () => {
    setError('')
    try {
      const body = {
        ...cliente,
        items: items.map((it) => ({
          productoId: it.productoId || null,
          concepto: it.concepto,
          cantidad: parseInt(it.cantidad, 10),
          precioUnitario: parseFloat(it.precioUnitario),
        })),
      }
      setCreada(await api.crearCotizacion(body))
    } catch (e) { setError(e.message) }
  }

  if (creada) {
    return (
      <section>
        <h2>Cotización {creada.numero} guardada</h2>
        <p>Total: <b>{money(creada.total)}</b></p>
        <a className="boton" href={pdfUrl(creada.id)} target="_blank" rel="noreferrer">Descargar / ver PDF</a>{' '}
        <button onClick={onTerminar}>Ir al panel</button>
      </section>
    )
  }

  const setC = (k) => (e) => setCliente({ ...cliente, [k]: e.target.value })

  return (
    <section>
      <h2>Nueva cotización</h2>
      <h3>Datos del cliente</h3>
      <div className="form-fila">
        <input placeholder="Nombre / Razón social *" value={cliente.clienteNombre} onChange={setC('clienteNombre')} />
        <input placeholder="RUC / DNI" value={cliente.clienteDocumento} onChange={setC('clienteDocumento')} />
        <input placeholder="Email" value={cliente.clienteEmail} onChange={setC('clienteEmail')} />
        <input placeholder="Teléfono" value={cliente.clienteTelefono} onChange={setC('clienteTelefono')} />
      </div>

      <h3>Ítems</h3>
      <table>
        <thead><tr><th>Producto</th><th>Descripción</th><th>Cant.</th><th>Precio</th><th>Subtotal</th><th></th></tr></thead>
        <tbody>
          {items.map((it, i) => (
            <tr key={i}>
              <td>
                <select value={it.productoId} onChange={(e) => elegirProducto(i, e.target.value)}>
                  <option value="">— Manual —</option>
                  {productos.map((p) => <option key={p.id} value={p.id}>{p.nombre}</option>)}
                </select>
              </td>
              <td><input value={it.concepto} onChange={(e) => setItem(i, { concepto: e.target.value })} /></td>
              <td><input type="number" min="1" value={it.cantidad} onChange={(e) => setItem(i, { cantidad: e.target.value })} className="corto" /></td>
              <td><input type="number" min="0" step="0.01" value={it.precioUnitario} onChange={(e) => setItem(i, { precioUnitario: e.target.value })} className="corto" /></td>
              <td className="num">{money(Number(it.cantidad || 0) * Number(it.precioUnitario || 0))}</td>
              <td><button onClick={() => setItems(items.filter((_, j) => j !== i))} disabled={items.length === 1}>✕</button></td>
            </tr>
          ))}
        </tbody>
      </table>
      <button onClick={() => setItems([...items, { ...ITEM }])}>+ Agregar ítem</button>

      <div className="totales">
        <div>Subtotal: <b>{money(subtotal)}</b></div>
        <div>IGV (18%): <b>{money(igv)}</b></div>
        <div className="total">Total: <b>{money(total)}</b></div>
      </div>

      {error && <p className="error">{error}</p>}
      <button className="primario" onClick={guardar}>Guardar cotización</button>{' '}
      <button onClick={onTerminar}>Cancelar</button>
    </section>
  )
}
