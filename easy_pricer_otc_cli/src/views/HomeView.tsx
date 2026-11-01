// Pagina iniziale, come quella del client degli strumenti quotati
const HomeView = () => (
    <div className="flex flex-column align-items-center justify-content-center h-full text-center p-5">
        <img src="/assets/lizard.png" alt="Logo" style={{ width: '250px', opacity: 0.8 }} className="mb-4" />

        <h1 className="text-4xl font-semibold text-800 mb-2">
            Welcome to So.Fi.E
        </h1>

        <p className="text-xl text-500 mb-6">
            Please select an instrument from the tree on the left to start.
        </p>

        <div className="mt-8 pt-5 border-top-1 surface-border w-full text-500 text-sm font-italic">
            Version 1.0 - by Softcaster Financial Engineering
        </div>
    </div>
);

export default HomeView;
