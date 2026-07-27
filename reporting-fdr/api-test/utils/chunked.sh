# https://everything.curl.dev/http/post/chunked

FILENAME=$1
curl  -H "Transfer-Encoding: chunked" --location --request POST -d @${FILENAME} 'https://api.uat.platform.pagopa.it/nodo/nodo-per-psp/v1' \
--header 'SOAPAction: nodoInviaFlussoRendicontazione' \
--header 'Content-Type: text/xml'


# --header 'Content-Type: text/xml' \
# --header 'Ocp-Apim-Subscription-Key: <Ocp-Apim-Subscription-Key>'