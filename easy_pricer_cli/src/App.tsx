import { useState, Suspense } from 'react';
import { BrowserRouter, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import { Tree } from 'primereact/tree';
import { ProgressSpinner } from 'primereact/progressspinner';
import type { TreeExpandedKeysType } from 'primereact/tree';
import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import { ActionProvider, useActions } from './context/ActionContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import {
  navigationNodes, ForexView, FxFutureView, CmdFutureView, HomeView, PlaceholderView,
  BondView, XNoteView, BondPView, BondFutureView, BondFuturePView, PositionProspectView
} from './config/navigation.config';
import { LoginDialog } from './components/fragments/LoginDialog';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { SystemDateProvider, useSystemDate } from './context/SystemDateContext';

const ToolbarWrapper = () => {
  const { onSave, onNew, onDel, onExport, isExporting, onCalculate, onRefresh, onPrint } = useActions(); // Hook che abbiamo creato prima
  const { user, logout } = useAuth();
  const { businessDate, loading: dateLoading } = useSystemDate();

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
      <Button
        icon="pi pi-trash"
        className="p-button-text p-button-danger p-1"
        onClick={() => {
          confirmDialog({
            message: 'Are you sure you want to delete this transaction?',
            header: 'Confirm Deletion',
            icon: 'pi pi-exclamation-triangle',
            acceptClassName: 'p-button-danger',
            accept: () => onDel?.(), // Esegue onDel solo se l'utente accetta
          });
        }}
        disabled={!onDel}
        tooltip="Delete"
      />

      <Button
        icon="pi pi-refresh"
        className="p-button-text p-button-plain p-1"
        onClick={() => onRefresh?.()}
        tooltip="Refresh"
      />

      <span className="border-left-1 surface-border mx-2"></span>
      <Button icon="pi pi-print" className="p-button-text p-button-plain p-1"
        onClick={() => onPrint?.()}
        disabled={!onPrint}
        tooltip="Print"
      />
      <Button
        icon={isExporting ? "pi pi-spin pi-spinner" : "pi pi-download"}
        className="p-button-text p-button-plain p-1"
        loading={isExporting}
        onClick={() => onExport?.()}
        disabled={!onExport || isExporting}
        tooltip="Export CSV" />

      <span className="border-left-1 surface-border mx-2"></span>
      <Button
        icon={"pi pi-calculator"}
        className="p-button-text p-button-plain p-1"
        onClick={() => onCalculate?.()}
        disabled={!onCalculate}
        tooltip="Calculate" />
    </div>
  );

  const rightContents = (

    <div className="flex align-items-center gap-3">
      {/* Mostriamola data */}
      <div className="flex align-items-center gap-2 mr-2 surface-100 px-2 py-1 border-round">
        <i className="pi pi-calendar text-500 text-xs"></i>
        <span className="text-xs font-semibold text-700">
          {dateLoading ? <i className="pi pi-spin pi-spinner text-xs"></i> : `System Date: ${businessDate}`}
        </span>
      </div>

      {/* Mostriamo il nome utente e il tasto Logout */}
      <div className="flex align-items-center gap-2 mr-2">
        <i className="pi pi-user text-400 text-xs"></i>
        <span className="text-sm font-medium text-600">{user?.username}</span>
      </div>

      <Button
        icon="pi pi-sign-out"
        className="p-button-text p-button-danger p-1"
        onClick={logout} // <--- Chiama la funzione logout del context
        tooltip="Logout"
      />
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
          onSelectionChange={(/*e: TreeSelectionEvent*/) => {
            // Se il nodo ha un URL nei metadati (data), navighiamo
            //if (e.node.data) navigate(e.node.data);
          }}
          expandedKeys={expandedKeys}
          onToggle={(e) => setExpandedKeys(e.value)}
          className="w-full border-none bg-transparent"

          nodeTemplate={(node, options) => {
            const isFolder = !!(node.children && node.children.length > 0);
            const icon = isFolder
              ? (options.expanded ? 'pi pi-folder-open' : 'pi pi-folder')
              : (node.icon || 'pi pi-file');

            return (
              <div className="flex align-items-center py-1">
                <i className={`${icon} mr-2 text-sm`}
                  style={{ color: isFolder ? '#f39c12' : '#ffffff' }}>
                </i>
                {/* Forza il colore del testo a bianco o grigio chiaro */}
                <span style={{ color: '#ecf0f1', fontSize: '13px' }}>
                  {node.label}
                </span>
              </div>
            );
          }}

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
              <Route path="/" element={<HomeView />} />
              <Route path="/forex" element={<ForexView />} />
              <Route path="/fxfuture" element={<FxFutureView />} />
              <Route path="/cmdfuture" element={<CmdFutureView />} />
              <Route path="/bond" element={<BondView />} />
              <Route path="/xnote" element={<XNoteView />} />
              <Route path="/bond-p" element={<BondPView />} />
              <Route path="/bondfuture" element={<BondFutureView />} />
              <Route path="/bondfuture-p" element={<BondFuturePView />} />
              <Route path="/position" element={<PositionProspectView />} />

              {/* Tutte le altre sezioni caricano il placeholder */}
              <Route path="/accounting" element={<PlaceholderView />} />
              <Route path="/user" element={<PlaceholderView />} />
              <Route path="/log" element={<PlaceholderView />} />

              {/* Pagina generica per URL inesistenti */}
              <Route path="*" element={<div>404 - Not Found</div>} />

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
      {/* AuthProvider deve avvolgere tutto ciò che usa useAuth */}
      <AuthProvider>
        <SystemDateProvider>
          <ActionProvider>
            <AppContent />
          </ActionProvider>
        </SystemDateProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

// Creiamo un piccolo componente interno per gestire la logica di visualizzazione
const AppContent = () => {
  const { user, login } = useAuth(); // Recuperiamo lo stato dal Context
  const handleLogin = (credentials: any) => {
    // Qui puoi aggiungere logica extra se serve, 
    // ma l'importante è chiamare il login del Context
    login(credentials.username);
  };

  return (
    <>
      {/* Il componente "ascoltatore" per le conferme */}
      <ConfirmDialog />

      {/* Mostriamo il Login se l'utente NON esiste nel context */}
      <LoginDialog visible={!user} onLogin={handleLogin} />

      {/* Mostriamo il Layout solo se l'utente è loggato */}
      {user && <MainLayout />}
    </>
  );
};
