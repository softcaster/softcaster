export interface ForexTrade {
    id: string;
    price: number;
    units: number;
    valueDate: Date | null;
    reference: string;
}
