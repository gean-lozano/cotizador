import { useEffect, useState } from 'react'
import { api, pdfUrl, money } from './api.js'

const ESTADOS = ['PENDIENTE', 'APROBADA', 'RECHAZADA']

export default function Cotizaciones({ onNueva }) {
  const [lista, setLista] = useState([])
  const [error, setError] = useState('')

  const cargar = () => api.cotizaciones().then(setLista).catch((e) => setError(e.message))
  useEffect(() => { cargar() }, [])

  const cambiar = async (id, estado) => {
    try { await api.cambiarEstado(id, estado); cargar() } catch (e) { setError(e.message) }
  }

  return (
    <section>
      <div className="barra">
        <h2>Cotizaciones registradas</h2>
        <button className="primario" onClick={onNueva}>+ Nueva cotización</button>
      </div>
      {error && <p className="error">{error}</p>}
      <table>
        <thead>
          <tr><th>N°</th><th>Cliente</th><th>Fecha</th><th>Total</th><th>Estado</th><th>PDF</th></tr>
        </thead>
        <tbody>
          {lista.length === 0 && <tr><td colSpan="6" className="vacio">Aún no hay cotizaciones</td></tr>}
          {lista.map((c) => (
            <tr key={c.id}>
              <td>{c.numero}</td>
              <td>{c.clienteNombre}</td>
              <td>{c.fecha}</td>
              <td className="num">{money(c.total)}</td>
              <td>
                <select value={c.estado} onChange={(e) => cambiar(c.id, e.target.value)} className={'estado ' + c.estado}>
                  {ESTADOS.map((s) => <option key={s}>{s}</option>)}
                </select>
              </td>
              <td><a href={pdfUrl(c.id)} target="_blank" rel="noreferrer">Ver PDF</a></td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  )
}
