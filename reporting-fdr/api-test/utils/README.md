# Invio flusso di rendicontazione (FdR)

Utility per generare e inviare un flusso di rendicontazione di test al Nodo dei Pagamenti (ambiente UAT).

## Cosa fa `nodoInviaFlussoRendicontazione.sh`

Lo script esegue in un unico comando l'intero flusso di test:

1. **Genera** un file XML SOAP `nodoInviaFlussoRendicontazione` con un numero configurabile di pagamenti (tramite `base64.js`).
2. **Invia** il file al Nodo dei Pagamenti in modalità chunked (tramite `chunked.sh`).

In pratica: **genera il flusso → lo invia** all'endpoint `https://api.uat.platform.pagopa.it/nodo/nodo-per-psp/v1`.

## Prerequisiti

- [Node.js](https://nodejs.org/) installato
- `bash` e `curl` disponibili nel terminale

## Come si usa

Dalla cartella `reporting-fdr/api-test/utils`:

```bash
# Invia un flusso con N pagamenti (es. 100)
bash nodoInviaFlussoRendicontazione.sh 100
```

Il primo argomento (`$1`) indica il **numero di pagamenti** da includere nel flusso.

### Parametri opzionali

Puoi personalizzare l'ente creditore destinatario (`identificativoDominio`) tramite variabile d'ambiente:

```bash
# 50 pagamenti destinati all'ente con dominio 12345678901
IDENTIFICATIVO_DOMINIO=12345678901 bash nodoInviaFlussoRendicontazione.sh 50
```

| Variabile | Descrizione | Default |
| --- | --- | --- |
| `$1` (argomento) | Numero di pagamenti nel flusso (`NUM_PAYMENTS`) | `100` |
| `IDENTIFICATIVO_DOMINIO` | Codice dell'ente creditore destinatario | `77777777777` |

Il file XML generato viene salvato con un nome del tipo:

```
<NUM_PAYMENTS>-<IDENTIFICATIVO_DOMINIO>-<identificativoFlusso>.xml
```

così da capire subito a quale ente creditore è destinato.

## Uso avanzato (passi separati)

Se vuoi eseguire i due passaggi manualmente:

```bash
# 1. Genera solo il file XML (stampa il nome del file creato)
NUM_PAYMENTS=100 node base64.js

# 2. Invia il file generato
bash chunked.sh <nome-file-generato>.xml
```
