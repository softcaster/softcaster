// src/components/fragments/SideSelector.tsx
import { SelectButton } from 'primereact/selectbutton';
import { SIDE_OPTIONS } from '../data/constants';

// --- Funzioni di conversione ---
// Trasforma il valore numerico nell'etichetta leggibile (opzionale, utile per i log)
export const getSideLabel = (value: number | null) => {
    return SIDE_OPTIONS.find(opt => opt.value === value)?.label || 'None';
};

// Trasforma il lato nel moltiplicatore (1 o -1)
export const getSideMultiplier = (value: number | null) => value || 1;

interface SideSelectorProps {
    value: number | null;
    onChange: (value: number) => void;
}

export const SideSelector = ({ value, onChange }: SideSelectorProps) => {
    return (
        <div className="flex flex-column gap-2">
            <SelectButton
                value={value as any}
                options={SIDE_OPTIONS}
                onChange={(e) => e.value && onChange(e.value)}
                // Usiamo SelectButton invece di Dropdown per un look più "software desktop"
                itemTemplate={(option) => (
                    <div className="flex align-items-center gap-2">
                        <i className={option.icon}></i>
                        <span>{option.label}</span>
                    </div>
                )}
            />
        </div>
    );
};
