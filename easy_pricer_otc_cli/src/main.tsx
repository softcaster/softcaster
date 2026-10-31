import 'primereact/resources/themes/lara-light-cyan/theme.css';  // Tema
import 'primereact/resources/primereact.min.css';                // Core CSS
import 'primeicons/primeicons.css';                             // Icone
import 'primeflex/primeflex.css';
import './App.css';
import './index.css';
import './otc.css';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { addLocale, locale } from 'primereact/api';
import App from './App.tsx';

// calendari e formati in italiano
addLocale('it', {
    firstDayOfWeek: 1,
    dayNames: ['domenica', 'lunedì', 'martedì', 'mercoledì', 'giovedì', 'venerdì', 'sabato'],
    dayNamesShort: ['dom', 'lun', 'mar', 'mer', 'gio', 'ven', 'sab'],
    dayNamesMin: ['D', 'L', 'M', 'M', 'G', 'V', 'S'],
    monthNames: ['gennaio', 'febbraio', 'marzo', 'aprile', 'maggio', 'giugno', 'luglio', 'agosto', 'settembre', 'ottobre', 'novembre', 'dicembre'],
    monthNamesShort: ['gen', 'feb', 'mar', 'apr', 'mag', 'giu', 'lug', 'ago', 'set', 'ott', 'nov', 'dic'],
    today: 'Oggi',
    clear: 'Cancella',
});
locale('it');

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <App />
    </StrictMode>,
);
