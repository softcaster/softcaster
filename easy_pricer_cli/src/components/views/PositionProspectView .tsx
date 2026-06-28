// src/components/views/PortfolioProspectView.tsx
// import React from 'react';
import { GenericProspectView } from './GenericProspectView';
import { ProspectFilterForm } from '../fragments/ProspectFilterForm';
import { PositionProspectTable } from '../fragments/PositionProspectTable';

// funzione reale che fa la POST tramite apiRequest
import { fetchPositionProspect } from '../services/services';

export const PositionProspectView = () => (
    <GenericProspectView
        fetchProspectData={fetchPositionProspect} 
        FilterComponent={ProspectFilterForm}
        TableComponent={PositionProspectTable}
    />
);
