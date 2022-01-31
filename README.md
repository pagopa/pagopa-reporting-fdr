# pagopa-reporting-fdr
Service to reading nodo-dei-pagamenti's fdr and publish it on pagoPA event hub

## Start the dev environment for reporting subsystem

### Docker

From `reporting-fdr` folder:

```
mv .env.example .env
```

From the project root:
```
docker-compose -f docker-compose-reporting.yml up --build
```

### Local

```
docker run -p 10000:10000 -p 10001:10001 -p 10002:10002 mcr.microsoft.com/azure-storage/azurite
```

From `reporting-fdr` folder:

```
cp local.settings.json.example local.settings.json
```

and then

```
mvn azure-functions:run
```

