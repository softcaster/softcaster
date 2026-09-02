import { Card } from 'primereact/card';

export const BondFutureDetails = ({ data }: { data: any }) => {
    if (!data) return null; // Non mostra nulla se non è selezionato un elemento

    // Funzione helper per le righe di dati (stile etichetta : valore)
    const InfoRow = ({ label, value }: { label: string, value: any }) => (
        <div className="flex justify-content-between py-2 border-bottom-1 surface-border">
            <span className="text-500 font-medium text-sm">{label}</span>
            <span className="text-900 font-bold text-sm">{value || '-'}</span>
        </div>
    );

    return (
        <div className="grid mt-4">
            {/* 1. GENERAL INFORMATION */}
            <div className="col-12 md:col-4">
                <Card title="GENERAL INFORMATION" className="h-full border-top-3 border-blue-500 shadow-2">
                    <InfoRow label="Type" value="Bond Futures" />
                    {/*
                    <InfoRow label="Sub type" value={data.assetClass.code} />
                    */}
                    <InfoRow label="ISIN Code" value={data.code} />
                    <InfoRow label="Issue description" value={data.description} />
                </Card>
            </div>

        </div>
    );
};
