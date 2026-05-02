import React, { createContext, useContext, useState } from 'react';

interface ActionContextType {
    onSave: (() => void) | null;
    onNew: (() => void) | null;
    onDel: (() => void) | null;
    onExport: (() => void) | null;
    isExporting: boolean;

    setAction: (actions: {
        save?: () => void,
        new?: () => void,
        del?: () => void,
        export?: () => void,
        isExporting?: boolean
    }) => void;
}

const ActionContext = createContext<ActionContextType | undefined>(undefined);

export const ActionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [onSave, setOnSave] = useState<(() => void) | null>(null);
    const [onNew, setOnNew] = useState<(() => void) | null>(null);
    const [onDel, setOnDel] = useState<(() => void) | null>(null);
    const [onExport, setOnExport] = useState<(() => void) | null>(null);
    const [isExporting, setIsExporting] = useState<boolean>(false);

    const setAction = (actions: {
        save?: () => void,
        new?: () => void,
        del?: () => void,
        export?: () => void,
        isExporting?: boolean;
    }) => {
        setOnSave(() => actions.save || null);
        setOnNew(() => actions.new || null);
        setOnDel(() => actions.del || null);
        setOnExport(() => actions.export || null);
        setIsExporting(actions.isExporting || false);
    };

    return (
        <ActionContext.Provider value={{ onSave, onNew, onDel, onExport, isExporting, setAction }}>
            {children}
        </ActionContext.Provider>
    );
};

export const useActions = () => {
    const context = useContext(ActionContext);
    if (!context) throw new Error("useActions must be used within ActionProvider");
    return context;
};
