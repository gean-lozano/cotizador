const API = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'

async function req(path, opts = {}) {
  const r = await fetch(API + path, { headers: { 'Content-Type': 'application/json' }, ...opts })
  if (!r.ok) {
    let msg = 'Error en el servidor'
    try { msg = (await r.json()).message || msg } catch { /* sin cuerpo JSON */ }
    throw new Error(msg)
  }
  return r.json()
}

const send = (method, body) => ({ method, body: body === undefined ? undefined : JSON.stringify(body) })

export const api = {
  cotizaciones: () => req('/cotizaciones'),
  crearCotizacion: (data) => req('/cotizaciones', send('POST', data)),
  cambiarEstado: (id, estado) => req(`/cotizaciones/${id}/estado`, send('PATCH', { estado })),
  productos: (soloActivos = false) => req(`/productos?soloActivos=${soloActivos}`),
  crearProducto: (p) => req('/productos', send('POST', p)),
  editarProducto: (id, p) => req(`/productos/${id}`, send('PUT', p)),
  alternarProducto: (id) => req(`/productos/${id}/activo`, send('PATCH')),
}

export const pdfUrl = (id) => `${API}/cotizaciones/${id}/pdf`
export const money = (n) => 'S/ ' + Number(n || 0).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
