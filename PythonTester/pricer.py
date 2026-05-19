import requests
from datetime import date
from dataclasses import asdict

# Importa la classe dal file dto.py posizionato nella stessa cartella
from dto import ForwardPricingRequest

# Configura l'URL del endpoint REST
BASE_URL = "http://localhost:8080/api/v1/pricing/future"

def main():
    """
    Punto di ingresso principale per il test dello script di pricing.
    """
    # 1. Istanzia la classe ForwardPricingRequest con i dati di test
    request_data = ForwardPricingRequest(
        isin="IT0024832682",       
        referencePrice=116.16,       
        referenceDate=date(2026, 5, 19), 
        domesticRate=0.01976,          
        foreignRate=0.0              
    )

    # 2. Converte la dataclass in un dizionario Python e formatta la data in stringa ISO
    payload = asdict(request_data)
    if payload["referenceDate"]:
        payload["referenceDate"] = payload["referenceDate"].isoformat()

    # 3. Esegue la chiamata POST al server REST all'interno del blocco try-except
    try:
        print(f"--- Avvio Test Pricing Future ---")
        print(f"Invio richiesta a: {BASE_URL}")
        print(f"Payload JSON inviato:\n{payload}\n")
        
        # Effettua la richiesta POST
        response = requests.post(BASE_URL, json=payload)
        
        # Solleva un'eccezione se il server risponde con un errore (es. 404, 500, ecc.)
        response.raise_for_status()
        
        # 4. Elaborazione della risposta del server
        print("--- Risposta ricevuta con successo dal Server REST ---")
        result_json = response.json()
        print(f"Dati grezzi ricevuti: {result_json}")
        
        # Esempio di estrazione del prezzo teorico
        if "theoreticalPrice" in result_json:
            print(f"\n-> PREZZO CALCOLATO: {result_json['theoreticalPrice']:.4f}")

    except requests.exceptions.ConnectionError:
        print("\n[ERRORE] Impossibile connettersi al server. Verifica che l'applicazione Java sia avviata e che la porta sia corretta.")
    except requests.exceptions.HTTPError as http_err:
        print(f"\n[ERRORE HTTP] Il server ha risposto con un errore: {http_err}")
        print(f"Dettaglio errore server: {response.text}")
    except Exception as e:
        print(f"\n[ERRORE GENERICO] Si è verificato un problema imprevisto: {e}")

# Questo costrutto indica a Python di eseguire la funzione main() 
# SOLO se il file viene lanciato direttamente (es. python pricer.py) 
# e non se viene importato in altri moduli.
if __name__ == "__main__":
    main()
