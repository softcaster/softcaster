import { Dropdown } from 'primereact/dropdown';
import { Calendar } from 'primereact/calendar';


// Frammento per Counterparty (identico per tutti)
interface CounterpartyFieldProps {
    value: any;
    options: any[];
    onChange: (val: any) => void; // <--- Specifica il tipo qui
}

export const CounterpartyField = ({ value, options, onChange }: CounterpartyFieldProps) => (
    <div className="col-12 md:col-3">
        <label className="text-sm font-bold block mb-2 text-600">Counterparty</label>
        <Dropdown
            value={value}
            options={options}
            dataKey="idCounterparty"
            optionLabel="description"
            onChange={(e) => onChange(e.value)}
            placeholder="Select Counterparty"
            filter
            className="w-full"
            // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
            valueTemplate={(option, props) => {
                if (option) {
                    return <span>{option.code}</span>;
                }
                return <span>{props.placeholder}</span>;
            }}

            // 2. Cosa mostrare nelle righe della lista quando è aperta
            itemTemplate={(option) => {
                return (
                    <div className="flex flex-column">
                        <span className="font-bold">{option.code}</span>
                        <small className="text-500">{option.description}</small>
                    </div>
                );
            }} />
    </div>
);

// Frammento per Positions (identico per tutti)
interface PositionFieldProps {
    value: any;
    options: any[];
    onChange: (val: any) => void; // <--- Specifica il tipo qui
}

export const PositionField = ({ value, options, onChange }: PositionFieldProps) => (
    <div className="col-12 md:col-3">
        <label className="text-sm font-bold block mb-2 text-600">Position</label>
        <Dropdown
            value={value}
            options={options}
            dataKey="idPosition"
            optionLabel="code"
            onChange={(e) => onChange(e.value)}
            placeholder="Select Position"
            filter
            className="w-full"

            // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
            valueTemplate={(option, props) => {
                if (option) {
                    return <span>{option.code}</span>;
                }
                return <span>{props.placeholder}</span>;
            }}

            // 2. Cosa mostrare nelle righe della lista quando è aperta
            itemTemplate={(option) => {
                return (
                    <div className="flex flex-column">
                        <span className="font-bold">{option.code}</span>
                        <small className="text-500">{option.description}</small>
                    </div>
                );
            }}
        />
    </div>
);

interface InstrumentFieldProps {
    label: string;
    value: any;
    options: any[];
    onChange: (value: any) => void; // Aggiungi il tipo qui
}

// Questo combo gestisce i vari strumenti Forex, FxFuture, Bond ...
export const InstrumentField = ({ label, value, options, onChange
}: InstrumentFieldProps) => (
    <div className="col-12 md:col-3">
        <label className="text-sm font-bold block mb-2">{label}</label>
        <Dropdown
            value={value}
            options={options}
            dataKey="idMasterData"
            optionLabel="code"
            onChange={(e) => onChange(e.value)}
            placeholder={`Select ${label}`}
            filter
            className="w-full"
            emptyMessage={options.length === 0 ? "Loading..." : "No data"}
            // 1. Cosa mostrare quando la combo è chiusa (elemento selezionato)
            valueTemplate={(option, props) => {
                if (option) {
                    return <span>{option.code}</span>;
                }
                return <span>{props.placeholder}</span>;
            }}

            // 2. Cosa mostrare nelle righe della lista quando è aperta
            itemTemplate={(option) => {
                return (
                    <div className="flex flex-column">
                        <span className="font-bold">{option.code}</span>
                        <small className="text-500">{option.description}</small>
                    </div>
                );
            }} />
    </div>
);

interface DateFieldProps {
    label: string;
    value: Date | string | null | undefined;
    onChange: (date: Date | null) => void;
    placeholder?: string;
}

export const DateField = ({ label, value, onChange, placeholder = "dd/mm/yyyy" }: DateFieldProps) => {
    // Gestione della conversione stringa -> Date per il componente Calendar
    const dateValue = value instanceof Date ? value : (value ? new Date(value) : null);

    return (
        <div className="col-12 md:col-3">
            <label className="text-sm font-bold block mb-2 text-600">{label}</label>
            <Calendar
                value={dateValue}
                onChange={(e) => onChange(e.value as Date | null)}
                showIcon
                dateFormat="dd/mm/yy"
                placeholder={placeholder}
                mask="99/99/9999" // Guida l'utente durante la digitazione
                className="w-full"
            />
        </div>
    );
};
