import { useState, useEffect } from 'react';
import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Message } from 'primereact/message'; // Importiamo Message

export const LoginDialog = ({ visible, onLogin }: any) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(false); // Stato per l'errore

    // Quando la Dialog diventa visibile (o viene montata), resetta tutto
    useEffect(() => {
        if (visible) {
            setUsername('');
            setPassword('');
            setError(false);
        }
    }, [visible]);

    const handleInternalLogin = () => {
        // Simulazione validazione (sostituisci con la tua logica)
        if (username === 'easypricer' && password === 'easypricer') {
            setError(false);
            onLogin({ username, password });
        } else {
            setError(true);
            // Rimuoviamo l'errore dopo 3 secondi (opzionale)
            setTimeout(() => setError(false), 3000);
        }
    };

    const footer = (
        <div className="flex justify-content-end gap-2">
            <Button label="Login" icon="pi pi-sign-in" className="p-button-sm p-button-info" onClick={handleInternalLogin} />
        </div>
    );

    return (
        <Dialog
            header="System Access"
            visible={visible}
            style={{ width: '400px' }}
            onHide={() => { }}
            footer={footer}
            closable={false}
            className={`login-dialog-custom ${error ? 'shake-error' : ''}`} // Classe dinamica per vibrazione
        >
            <div className="flex flex-column gap-4 py-2">
                <div className="text-center mb-2">
                    <div className="inline-flex align-items-center justify-content-center border-circle bg-blue-50"
                        style={{ width: '80px', height: '80px' }}>
                        <i className="pi pi-lock text-4xl text-blue-600"></i>
                    </div>
                    <p className="text-700 font-semibold mt-3 mb-0">System Access</p>
                    <p className="text-500 text-xs mt-1">Enter your credentials to log in</p>
                </div>
                {/* Messaggio di errore PrimeReact */}
                {error && (
                    <Message severity="error" text="Invalid credentials" className="w-full justify-content-start" />
                )}

                <div className="p-float-label mt-2">
                    <InputText id="username" value={username}
                        onChange={(e) => { setUsername(e.target.value); setError(false); }}
                        className={`w-full ${error ? 'p-invalid' : ''}`}
                    />
                    <label htmlFor="username">Username</label>
                </div>

                <div className="p-float-label">
                    <Password
                        id="password"
                        value={password}
                        onChange={(e) => { setPassword(e.target.value); setError(false); }}
                        toggleMask
                        feedback={false}
                        className="w-full"
                        inputClassName={`w-full ${error ? 'p-invalid' : ''}`}
                    />
                    <label htmlFor="password">Password</label>
                </div>
            </div>
        </Dialog>
    );
};
