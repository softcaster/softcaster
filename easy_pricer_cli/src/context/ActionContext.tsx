import React, { createContext, useContext, useState } from 'react';

interface ActionContextType {
    onSave: (() => void) | null;
    onNew: (() => void) | null;
    onDel: (() => void) | null;
    setAction: (actions: { save?: () => void, new?: () => void, del?: () => void }) => void;
}

const ActionContext = createContext<ActionContextType | undefined>(undefined);

export const ActionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [onSave, setOnSave] = useState<(() => void) | null>(null);
    const [onNew, setOnNew] = useState<(() => void) | null>(null);
    const [onDel, setOnDel] = useState<(() => void) | null>(null);

    const setAction = (actions: { save?: () => void, new?: () => void, del?: () => void }) => {
        if (actions.save) setOnSave(() => actions.save);
        if (actions.new) setOnNew(() => actions.new);
        if (actions.del) setOnDel(() => actions.del);
    };

    return (
        <ActionContext.Provider value={{ onSave, onNew, onDel, setAction }}>
            {children}
        </ActionContext.Provider>
    );
};

export const useActions = () => {
    const context = useContext(ActionContext);
    if (!context) throw new Error("useActions must be used within ActionProvider");
    return context;
};
