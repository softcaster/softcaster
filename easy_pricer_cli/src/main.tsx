import "primereact/resources/themes/lara-light-cyan/theme.css";  // Tema
import "primereact/resources/primereact.min.css";                // Core CSS
import "primeicons/primeicons.css";                             // Icone
import "primeflex/primeflex.css";  
import './App.css'
import './index.css'
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
