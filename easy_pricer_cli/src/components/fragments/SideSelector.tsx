// src/components/fragments/SideSelector.tsx
import { SelectButton } from 'primereact/selectbutton';
import { SIDE_OPTIONS } from '../data/constants';

interface SideSelectorProps {
    value: number | null;
    onChange: (value: number) => void;
}

export const SideSelector = ({ value, onChange }: SideSelectorProps) => {
    return (
        <div className="flex flex-column gap-2">
            <label className="text-sm font-bold text-600 uppercase">Side</label>
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
