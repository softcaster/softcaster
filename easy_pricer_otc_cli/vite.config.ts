import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// Client degli strumenti OTC: stessa struttura di easy_pricer_cli, porta 3001
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  build: {
    rollupOptions: {
      output: {
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
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,
      },
    },
  },
  server: {
    port: 3001,
    open: true,
  },
});
