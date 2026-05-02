import { createContext, useContext, useState } from 'react';
import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';

interface User {
    username: string;
    loginTime: Date;
}

interface AuthContextType {
    user: User | null;
    login: (username: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<User | null>(null);
    const navigate = useNavigate(); // Hook per la navigazione

    const login = (username: string) => {
        setUser({ username, loginTime: new Date() });
    };

    const logout = () => {
        setUser(null);     // 1. Resetta lo stato dell'utente
        navigate('/');    // 2. Reindirizza alla Home (o al login)
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within an AuthProvider");
    return context;
};
