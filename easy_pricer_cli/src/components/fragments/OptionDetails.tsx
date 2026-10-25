import { Card } from 'primereact/card';
import type { OptionSeriesDto } from '../services/services.ts';

// famiglia della testata -> ramo e modello usato dal server
const FAMILIES: Record<string, { type: string, model: string }> = {
    FFO: { type: 'Fx Future Option', model: 'Black-76 on the future' },
    BFO: { type: 'Bond Future Option', model: 'Black-76 on the future' },
    FXO: { type: 'Fx Option', model: 'Garman-Kohlhagen' },
    BDO: { type: 'Bond Option', model: 'Black-76 on the bond forward' }
};

// Riga etichetta : valore
const InfoRow = ({ label, value }: { label: string, value?: string }) => (
    <div className="flex justify-content-between py-2 border-bottom-1 surface-border">
        <span className="text-500 font-medium text-sm">{label}</span>
        <span className="text-900 font-bold text-sm">{value || '-'}</span>
    </div>
);

export const OptionDetails = ({ data }: { data?: OptionSeriesDto }) => {
    if (!data) return null; // Non mostra nulla se non è selezionato un elemento

    const family = FAMILIES[data.assetClass];

    return (
        <div className="grid mt-4">
            <div className="col-12 md:col-4">
                <Card title="GENERAL INFORMATION" className="h-full border-top-3 border-blue-500 shadow-2">
                    <InfoRow label="Type" value={family?.type} />
                    <InfoRow label="Family" value={data.assetClass} />
                    <InfoRow label="Series code" value={data.code} />
                    <InfoRow label="Description" value={data.description} />
                    <InfoRow label="Model (european)" value={family?.model} />
                    <InfoRow label="Model (american)" value="CRR binomial" />
                </Card>
            </div>
        </div>
    );
};
