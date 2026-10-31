import { Suspense, useState } from 'react';
import { BrowserRouter, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { Button } from 'primereact/button';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Toolbar } from 'primereact/toolbar';
import { Tree } from 'primereact/tree';
import type { TreeExpandedKeysType } from 'primereact/tree';
import {
    CmdAccumulatorPricingView, CmdAccumulatorView, ContractsView, FxAccumulatorPricingView, FxAccumulatorView,
    HomeView, LoanFixedView, LoanFloatingView, LoanPricingView, navigationNodes,
} from './config/navigation.config';
import { ActionProvider, useActions } from './context/ActionContext';
import { AuthProvider, useAuth } from './context/AuthContext';
import { SystemDateProvider, useSystemDate } from './context/SystemDateContext';
import { LoginDialog, type LoginCredentials } from './fragments/LoginDialog';

// Stessa struttura del client degli strumenti quotati: toolbar con le azioni
// della vista corrente (ActionContext), explorer ad albero, login (AuthContext)
// e data di sistema (SystemDateContext)

const ToolbarWrapper = () => {
    const { onSave, onNew, onDel, onExport, isExporting, onCalculate, onRefresh, onPrint } = useActions();
    const { user, logout } = useAuth();
    const { businessDate, loading: dateLoading } = useSystemDate();

    const leftContents = (
        <div className="flex gap-2">
            <Button icon="pi pi-plus" className="p-button-text p-button-plain p-1" onClick={() => onNew?.()} disabled={!onNew} tooltip="New" />
            <Button icon="pi pi-save" className="p-button-text p-button-plain p-1" onClick={() => onSave?.()} disabled={!onSave} tooltip="Save" />
            <Button
                icon="pi pi-trash"
                className="p-button-text p-button-danger p-1"
                onClick={() => {
                    confirmDialog({
                        message: 'Are you sure you want to delete this contract?',
                        header: 'Confirm Deletion',
                        icon: 'pi pi-exclamation-triangle',
                        acceptClassName: 'p-button-danger',
                        accept: () => onDel?.(),
                    });
                }}
                disabled={!onDel}
                tooltip="Delete"
            />
            <Button icon="pi pi-refresh" className="p-button-text p-button-plain p-1" onClick={() => onRefresh?.()} disabled={!onRefresh} tooltip="Refresh" />

            <span className="border-left-1 surface-border mx-2"></span>
            <Button icon="pi pi-print" className="p-button-text p-button-plain p-1" onClick={() => onPrint?.()} disabled={!onPrint} tooltip="Print" />
            <Button
                icon={isExporting ? 'pi pi-spin pi-spinner' : 'pi pi-download'}
                className="p-button-text p-button-plain p-1"
                loading={isExporting}
                onClick={() => onExport?.()}
                disabled={!onExport || isExporting}
                tooltip="Export CSV" />

            <span className="border-left-1 surface-border mx-2"></span>
            <Button icon="pi pi-calculator" className="p-button-text p-button-plain p-1" onClick={() => onCalculate?.()} disabled={!onCalculate} tooltip="Calculate" />
        </div>
    );

    const rightContents = (
        <div className="flex align-items-center gap-3">
            <div className="flex align-items-center gap-2 mr-2 surface-100 px-2 py-1 border-round">
                <i className="pi pi-calendar text-500 text-xs"></i>
                <span className="text-xs font-semibold text-700">
                    {dateLoading ? <i className="pi pi-spin pi-spinner text-xs"></i> : `System Date: ${businessDate}`}
                </span>
            </div>
            <div className="flex align-items-center gap-2 mr-2">
                <i className="pi pi-user text-400 text-xs"></i>
                <span className="text-sm font-medium text-600">{user?.username}</span>
            </div>
            <Button icon="pi pi-sign-out" className="p-button-text p-button-danger p-1" onClick={logout} tooltip="Logout" />
        </div>
    );

    return <Toolbar start={leftContents} end={rightContents} className="p-1 border-none border-bottom-1" />;
};

const MainLayout = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // la chiave di un nodo foglia e' il percorso + '-key'
    const currentKey = location.pathname.substring(1) + '-key';

    const [expandedKeys, setExpandedKeys] = useState<TreeExpandedKeysType>({ trades: true, 'cash-flow': true, forex: true, commodity: true });

    return (
        <div className="flex h-screen overflow-hidden bg-white">
            <div className="w-20rem custom-sidebar border-right-1 flex flex-column h-full">
                <div className="p-3 font-bold border-bottom-1 text-sm uppercase tracking-wider">
                    Explorer OTC
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
                                <i className={`${icon} mr-2 text-sm`} style={{ color: isFolder ? '#f39c12' : '#ffffff' }}></i>
                                <span style={{ color: '#ecf0f1', fontSize: '13px' }}>{node.label}</span>
                            </div>
                        );
                    }}
                />
            </div>

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
                            <Route path="/loan-fixed" element={<LoanFixedView />} />
                            <Route path="/loan-floating" element={<LoanFloatingView />} />
                            <Route path="/fx-accumulator" element={<FxAccumulatorView />} />
                            <Route path="/cmd-accumulator" element={<CmdAccumulatorView />} />
                            <Route path="/loan-p" element={<LoanPricingView />} />
                            <Route path="/fx-accumulator-p" element={<FxAccumulatorPricingView />} />
                            <Route path="/cmd-accumulator-p" element={<CmdAccumulatorPricingView />} />
                            <Route path="/contracts" element={<ContractsView />} />
                            <Route path="*" element={<div className="p-4">404 - Not Found</div>} />
                        </Routes>
                    </Suspense>
                </div>
            </div>
        </div>
    );
};

const AppContent = () => {
    const { user, login } = useAuth();
    const handleLogin = (credentials: LoginCredentials) => login(credentials.username);

    return (
        <>
            <ConfirmDialog />
            <LoginDialog visible={!user} onLogin={handleLogin} />
            {user && <MainLayout />}
        </>
    );
};

export default function App() {
    return (
        <BrowserRouter>
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
