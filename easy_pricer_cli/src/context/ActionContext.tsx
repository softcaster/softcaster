import React, { createContext, useContext, useState } from 'react';
import { Toast } from 'primereact/toast';
import type { ToastMessage } from 'primereact/toast';
import { useRef } from 'react';

interface ActionContextType {
    onSave: (() => void) | null;
    onNew: (() => void) | null;
    onDel: (() => void) | null;
    onExport: (() => void) | null;
    isExporting: boolean;
    onCalculate: (() => void) | null;
    showToast: (message: ToastMessage) => void;
    setAction: (actions: {
        save?: () => void,
        new?: () => void,
        del?: () => void,
        export?: () => void,
        isExporting?: boolean,
        calculate?: () => void
    }) => void;
}

const ActionContext = createContext<ActionContextType | undefined>(undefined);

export const ActionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [onSave, setOnSave] = useState<(() => void) | null>(null);
    const [onNew, setOnNew] = useState<(() => void) | null>(null);
    const [onDel, setOnDel] = useState<(() => void) | null>(null);
    const [onExport, setOnExport] = useState<(() => void) | null>(null);
    const [isExporting, setIsExporting] = useState<boolean>(false);
    const [onCalculate, setOnCalculate] = useState<(() => void) | null>(null);

    const toast = useRef<Toast>(null);
    const showToast = (message: ToastMessage) => {
        toast.current?.show(message);
    };

    const setAction = (actions: {
        save?: () => void,
        new?: () => void,
        del?: () => void,
        export?: () => void,
        isExporting?: boolean,
        calculate?: () => void,
    }) => {
        setOnSave(() => actions.save || null);
        setOnNew(() => actions.new || null);
        setOnDel(() => actions.del || null);
        setOnExport(() => actions.export || null);
        setIsExporting(actions.isExporting || false);
        setOnCalculate(() => actions.calculate || null);
    };

    return (
        <ActionContext.Provider value={{ onSave, onNew, onDel, onExport, isExporting, onCalculate, showToast, setAction }}>
            <Toast ref={toast} /> {/* Il Toast vive qui, nel cuore dell'app */}
            {children}
        </ActionContext.Provider>
    );
};

export const useActions = () => {
    const context = useContext(ActionContext);
    if (!context) throw new Error("useActions must be used within ActionProvider");
    return context;
};
