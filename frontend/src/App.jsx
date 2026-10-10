import { useEffect, useState } from 'react'
import Cotizaciones from './Cotizaciones.jsx'
import Productos from './Productos.jsx'
import NuevaCotizacion from './NuevaCotizacion.jsx'

export default function App() {
  const [vista, setVista] = useState('panel')
  const [tema, setTema] = useState(
    localStorage.getItem('tema') ||
      (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light')
  )

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', tema)
    localStorage.setItem('tema', tema)
  }, [tema])

  const tab = (id, texto) => (
    <button className={vista === id ? 'tab activa' : 'tab'} onClick={() => setVista(id)}>{texto}</button>
  )

  return (
    <div className="app">
      <header>
        <h1>Sistema de Cotizaciones</h1>
        <nav>
          {tab('panel', 'Cotizaciones')}
          {tab('nueva', 'Nueva cotización')}
          {tab('productos', 'Productos y servicios')}
          <button className="tab" onClick={() => setTema(tema === 'dark' ? 'light' : 'dark')}>
            {tema === 'dark' ? '☀️ Claro' : '🌙 Oscuro'}
          </button>
        </nav>
      </header>
      <main>
        {vista === 'panel' && <Cotizaciones onNueva={() => setVista('nueva')} />}
        {vista === 'nueva' && <NuevaCotizacion onTerminar={() => setVista('panel')} />}
        {vista === 'productos' && <Productos />}
      </main>
    </div>
  )
}
