package it.gov.pagopa.fdr.service;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

public class HealthCheckService {

    private final String storageConnectionString;
    private final String containerBlobOutName;
    private final String containerBlobInName;

    /** Constructor that reads parameters from environment variables. */
    public HealthCheckService() {
        this(
            System.getenv("FLOW_SA_CONNECTION_STRING"),
            System.getenv("FLOWS_XML_BLOB"),
            System.getenv("OUTPUT_BLOB")
        );
    }

    /** Constructor with parameter injection. */
    public HealthCheckService(String storageConnectionString, String containerBlobInName, String containerBlobOutName) {
        this.storageConnectionString = storageConnectionString;
        this.containerBlobInName = containerBlobInName;
        this.containerBlobOutName = containerBlobOutName;
    }

    public boolean checkConnection() throws URISyntaxException, StorageException, InvalidKeyException {

        if (isBlank(storageConnectionString) || isBlank(containerBlobInName) || isBlank(containerBlobOutName)) {
            return false;
        }

        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobContainer containerBlobIn =
            storageAccount.createCloudBlobClient().getContainerReference(containerBlobInName);
        CloudBlobContainer containerBlobOut =
            storageAccount.createCloudBlobClient().getContainerReference(containerBlobOutName);

        return containerBlobIn.exists() && containerBlobOut.exists();
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}