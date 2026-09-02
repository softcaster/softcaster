import { Card } from 'primereact/card';
import type {
    SecurityMasterDataDto
} from '../services/dto';

export const BondDetails = ({ data }: { data: SecurityMasterDataDto }) => {
    if (!data) return null; // Non mostra nulla se non è selezionato un bond

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
                    <InfoRow label="Type" value="Bonds" />
                    <InfoRow label="Sub type" value={data.assetClass} />
                    <InfoRow label="ISIN Code" value={data.code} />
                    <InfoRow label="Issue description" value={data.description} />
                </Card>
            </div>

            {/* 2. ISSUER INFORMATION */}
            <div className="col-12 md:col-4">
                <Card title="ISSUER INFORMATION" className="h-full border-top-3 border-orange-500 shadow-2">
                    <InfoRow label="Issuer name" value={data.shortIssuerName} />
                    <InfoRow label="Issuer Type" value={data.longIssuerName} />
                    <InfoRow label="Issuer country" value={data.country} />
                </Card>
            </div>

            {/* 3. TRADING CHARACTERISTICS */}
            <div className="col-12 md:col-4">
                <Card title="TRADING CHARACTERISTICS" className="h-full border-top-3 border-green-500 shadow-2">
                    <InfoRow label="First listing" value={data.issueDate} />
                    <InfoRow label="Price notation" value="In %" />
                    <InfoRow label="Trading Lot" value={1000.} />
                    <InfoRow label="Tick size" value={100.} />
                </Card>
            </div>
            {/* 3. TRADING CHARACTERISTICS */}
            <div className="col-12 md:col-4">
                <Card title="COUPON" className="h-full border-top-3 border-blue-500 shadow-2">
                    <InfoRow label="Rate %" value={data.interestRate} />
                    <InfoRow label="Frequency" value={data.frequency} />
                    <InfoRow label="First coupon date" value={data.firstCouponPaymentDate} />
                    <InfoRow label="First coupon" value={data.firstCouponRate} />
                </Card>
            </div>
        </div>
    );
};
