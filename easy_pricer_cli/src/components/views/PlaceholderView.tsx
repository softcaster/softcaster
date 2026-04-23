import React from 'react';
import { useLocation } from 'react-router-dom';

const PlaceholderView: React.FC = () => {
    const location = useLocation();

    // Estraiamo il nome della rotta per personalizzare il messaggio
    const viewName = location.pathname.substring(1).toUpperCase();

    return (
        <div className="flex flex-column align-items-center justify-content-center h-full text-center p-5 surface-50">
            {/* Icona di PrimeIcons con animazione pulsante */}
            <i className="pi pi-cog p-5 text-6xl text-400 mb-4 animate-pulse"
                style={{ border: '2px dashed #ccc', borderRadius: '50%' }}></i>

            <h2 className="text-3xl font-light text-700 mb-2">
                Section: <span className="font-bold text-blue-600">{viewName || 'UNKNOWN'}</span>
            </h2>

            <p className="text-xl text-500 mb-4">
                This module is currently under development.
            </p>

            <div className="p-3 border-round bg-white shadow-1 text-500 text-sm">
                <i className="pi pi-info-circle mr-2"></i>
                Technical Team is working to connect this view to the database.
            </div>
        </div>
    );
};

export default PlaceholderView;
