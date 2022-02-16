# https://everything.curl.dev/http/post/chunked


curl  -H "Transfer-Encoding: chunked" --location --request POST -d @10-2022-02-16AGID_01-S760395078.xml 'https://api.uat.platform.pagopa.it/nodo/nodo-per-psp/v1' \
--header 'SOAPAction: nodoInviaFlussoRendicontazione' \
--header 'Content-Type: application/xml'