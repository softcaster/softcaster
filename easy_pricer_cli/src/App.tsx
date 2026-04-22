import { useState, Suspense } from 'react';
import { BrowserRouter, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { Tree } from 'primereact/tree';
import type { TreeSelectionEvent } from 'primereact/tree';
import { ProgressSpinner } from 'primereact/progressspinner';
import type { TreeExpandedKeysType } from 'primereact/tree';
import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import { ActionProvider, useActions } from './context/ActionContext';
import { navigationNodes, ForexView, FxFutureView } from './config/navigation.config';

const ToolbarWrapper = () => {
  const { onSave, onNew } = useActions(); // Hook che abbiamo creato prima

  const leftContents = (
    <div className="flex gap-2">
      <Button
        icon="pi pi-plus"
        className="p-button-text p-button-plain p-1"
        onClick={() => onNew?.()}
        disabled={!onNew}
        tooltip="New"
      />
      <Button
        icon="pi pi-save"
        className="p-button-text p-button-plain p-1"
        onClick={() => onSave?.()}
        disabled={!onSave}
        tooltip="Save"
      />
      <span className="border-left-1 surface-border mx-2"></span>
      <Button icon="pi pi-print" className="p-button-text p-button-plain p-1" tooltip="Print" />
      <Button icon="pi pi-download" className="p-button-text p-button-plain p-1" tooltip="Export" />
    </div>
  );

  const rightContents = (
    <div className="flex align-items-center gap-2">
      <i className="pi pi-search text-400"></i>
      <span className="text-sm text-500">Search...</span>
    </div>
  );

  return (
    <Toolbar
      start={leftContents}
      end={rightContents}
      className="p-1 border-none border-bottom-1"
    />
  );
};

const MainLayout = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // Sincronizziamo la selezione del Tree con l'URL corrente
  // Se l'URL è /forex, la chiave selezionata sarà 'forex-key'
  const currentKey = location.pathname.substring(1) + '-key';

  const [expandedKeys, setExpandedKeys] = useState<TreeExpandedKeysType>({ 'root': true });

  return (
    <div className="flex h-screen overflow-hidden bg-white">
      {/* SIDEBAR SCURA */}
      <div className="w-20rem custom-sidebar border-right-1 flex flex-column h-full">
        <div className="p-3 font-bold border-bottom-1 text-sm uppercase tracking-wider">
          Explorer
        </div>
        <Tree
          value={navigationNodes}
          selectionMode="single"
          selectionKeys={currentKey}
          onSelect={(e) => {
            if (e.node.data) {
              navigate(e.node.data);
            }
          }}
          onSelectionChange={(e: TreeSelectionEvent) => {
            // Se il nodo ha un URL nei metadati (data), navighiamo
            //if (e.node.data) navigate(e.node.data);
          }}
          expandedKeys={expandedKeys}
          onToggle={(e) => setExpandedKeys(e.value)}
          className="w-full border-none bg-transparent"
        />
      </div>

      {/* AREA CONTENUTO PRINCIPALE */}
      <div className="flex-grow-1 flex flex-column bg-white">
        <ToolbarWrapper />

        <div className="flex-grow-1 overflow-hidden relative">
          <Suspense fallback={
            <div className="flex justify-content-center align-items-center h-full">
              <ProgressSpinner style={{ width: '40px' }} />
            </div>
          }>
            <Routes>
              <Route path="/forex" element={<ForexView />} />
              <Route path="/fxfuture" element={<FxFutureView />} />
              {/* Default Page */}
              <Route path="/" element={
                <div className="flex flex-column align-items-center justify-content-center h-full text-400">
                  <i className="pi pi-mouse-pointer text-4xl mb-3"></i>
                  <p>Seleziona un elemento dal menu a sinistra per iniziare</p>
                </div>
              } />
            </Routes>
          </Suspense>
        </div>
      </div>
    </div>
  );
};

export default function App() {
  return (
    <BrowserRouter>
      <ActionProvider>
        <MainLayout />
      </ActionProvider>
    </BrowserRouter>
  );
}
