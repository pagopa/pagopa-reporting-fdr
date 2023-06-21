package it.gov.pagopa.fdr.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

public class HealthCheckService {

    private final String storageConnectionString = System.getenv("FLOW_SA_CONNECTION_STRING");
    private final String containerBlobOutName = System.getenv("OUTPUT_BLOB");
    private final String containerBlobInName = System.getenv("FLOWS_XML_BLOB");

    public boolean checkConnection() throws URISyntaxException, StorageException, InvalidKeyException {
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobContainer containerBlobIn = storageAccount.createCloudBlobClient().getContainerReference(containerBlobInName);
        CloudBlobContainer containerBlobOut = storageAccount.createCloudBlobClient().getContainerReference(containerBlobOutName);

        return containerBlobIn.exists() && containerBlobOut.exists();
    }
}
