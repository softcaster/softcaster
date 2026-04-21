import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// https://vitejs.dev
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      // Questo ti permette di usare "@/components/..." invece di "../../../components/..."
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    // Ottimizzazione del bundle
    rollupOptions: {
      output: {
        // Usiamo una funzione invece di un oggetto fisso.
        // È il modo più sicuro per evitare errori di tipi in TS.
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('primereact')) {
              return 'primereact-vendor';
            }
            return 'vendor';
          }
        }
      },
    },
    // Rimpicciolisce il codice finale per la produzione
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true, // Rimuove i console.log in produzione
      },
    },
  },
  server: {
    port: 3000, // Imposta una porta fissa, comoda per lo sviluppo
    open: true, // Apre automaticamente il browser all'avvio
  },
});
