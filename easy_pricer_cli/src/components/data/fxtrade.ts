export interface ForexTrade {
    id: string;
    currPair: string;
    price: number;
    units: number;
    valueDate: Date | null;
    reference: string;
}
