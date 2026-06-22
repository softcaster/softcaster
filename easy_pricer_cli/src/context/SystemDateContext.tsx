import React, { createContext, useState, useEffect, useContext } from 'react';

interface SystemDateContextType {
  businessDate: string | null;
  loading: boolean;
}

const SystemDateContext = createContext<SystemDateContextType>({ businessDate: null, loading: true });

export const SystemDateProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [businessDate, setBusinessDate] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Chiamata all'endpoint del tuo server Spring Boot (Opzione A/B viste in precedenza)
    fetch('http://localhost:8080/api/v1/internal/system/official_date')
      .then((response) => {
        if (!response.ok) throw new Error('Network response was not ok');
        // Se restituisce un JSON es: { "businessDate": "2026-06-22" }
        return response.text();
      })
      .then((dataStr) => {
        setBusinessDate(dataStr);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Failed to load business date:", error);
        setLoading(false);
      });
  }, []);

  return (
    <SystemDateContext.Provider value={{ businessDate, loading }}>
      {children}
    </SystemDateContext.Provider>
  );
};

export const useSystemDate = () => useContext(SystemDateContext);
