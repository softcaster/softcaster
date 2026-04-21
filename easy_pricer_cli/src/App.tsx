import { useState, Suspense } from 'react';
import { Tree } from 'primereact/tree';
import type { TreeSelectionEvent } from 'primereact/tree';
import { ProgressSpinner } from 'primereact/progressspinner';
import type { TreeExpandedKeysType } from 'primereact/tree';
import { Toolbar } from 'primereact/toolbar';
import { Button } from 'primereact/button';
import { ActionProvider, useActions } from './context/ActionContext';
import { navigationNodes, viewMap } from './config/navigation.config';

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

export default function App() {
  const [selectedKey, setSelectedKey] = useState<TreeSelectionEvent['value']>(null);
  const [expandedKeys, setExpandedKeys] = useState<TreeExpandedKeysType>({ 'root': false });

  // La logica rimane identica, ma i dati sono esterni
  const activeKey = typeof selectedKey === 'string' ? selectedKey : null;
  const ActiveView = activeKey ? viewMap[activeKey] : null;

  return (
    <ActionProvider>
      <div className="flex h-screen overflow-hidden bg-white">
        {/* Sidebar con Tree */}
        <div className="w-20rem custom-sidebar border-right-1 flex flex-column h-full">
          <div className="p-3 font-bold border-bottom-1">Explorer</div>
          <Tree
            value={navigationNodes}
            selectionMode="single"
            selectionKeys={selectedKey}
            onSelectionChange={(e: TreeSelectionEvent) => setSelectedKey(e.value)}
            // Chiusura Item padre
            expandedKeys={expandedKeys}
            onToggle={(e) => setExpandedKeys(e.value)}
            className="w-full border-none bg-transparent"
          />
        </div>

        {/* Main Content Dinamico */}
        <div className="flex-grow-1 flex flex-column bg-white">
          {/* TOOLBAR SUPERIORE */}
          <ToolbarWrapper />
          {/* FORM + TABLE */}
          <div className="flex-grow-1 overflow-y-auto relative">
            {ActiveView ? (
              <Suspense fallback={
                <div className="flex justify-content-center align-items-center h-full">
                  <ProgressSpinner />
                </div>
              }>
                <ActiveView />
              </Suspense>
            ) : (
              <div className="flex flex-column align-items-center justify-content-center h-full text-400">
                <i className="pi pi-mouse-pointer text-4xl mb-3"></i>
                <p>Seleziona un elemento dal menu a sinistra</p>
              </div>
            )}
          </div>
        </div>
      </div>
    </ActionProvider>
  );
}
