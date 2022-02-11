#!/bin/bash

newman run -n 3 api-test/FdrReporting.postman_collection.json --environment=api-test/Apim4Nodo.postman_environment.json --reporters cli,junit --reporter-junit-export Results/FDR-TEST.xml



