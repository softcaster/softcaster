import React from 'react';

const HomeView: React.FC = () => {
    return (
        <div className="flex flex-column align-items-center justify-content-center h-full text-center p-5">
            {/* Immagine Centrale */}
            <img
                src="/assets/lizard.png" 
                alt="Logo"
                style={{ width: '250px', opacity: 0.8 }}
                className="mb-4"
            />

            {/* Titolo Principale */}
            <h1 className="text-4xl font-semibold text-800 mb-2">
                Welcome to So.Fi.E
            </h1>

            {/* Sottotitolo */}
            <p className="text-xl text-500 mb-6">
                Please select an instrument from the tree on the left to start.
            </p>

            {/* Footer della Home */}
            <div className="mt-8 pt-5 border-top-1 surface-border w-full text-500 text-sm font-italic">
                Version 1.0 - by Softcaster Financial Engineering
            </div>
        </div>
    );
};

export default HomeView;
