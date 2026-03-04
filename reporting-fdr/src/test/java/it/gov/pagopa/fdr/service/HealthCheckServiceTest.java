package it.gov.pagopa.fdr.service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HealthCheckServiceTest {

    private MockedStatic<CloudStorageAccount> mockedStatic;

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) mockedStatic.close();
    }

    @Test
    void shouldReturnFalseIfAnyConfigIsNull() throws Exception {
        HealthCheckService service = new HealthCheckService(null, "in", "out");
        assertFalse(service.checkConnection());
    }

    @Test
    void shouldReturnFalseIfAnyConfigIsBlank() throws Exception {
        HealthCheckService service = new HealthCheckService("   ", "in", "out");
        assertFalse(service.checkConnection());
    }

    @Test
    void shouldReturnTrueWhenBothContainersExist() throws Exception {
        HealthCheckService service = new HealthCheckService("fake", "in", "out");

        CloudStorageAccount accountMock = mock(CloudStorageAccount.class);
        CloudBlobClient blobClientMock = mock(CloudBlobClient.class);
        CloudBlobContainer containerInMock = mock(CloudBlobContainer.class);
        CloudBlobContainer containerOutMock = mock(CloudBlobContainer.class);

        mockedStatic = mockStatic(CloudStorageAccount.class);
        mockedStatic.when(() -> CloudStorageAccount.parse("fake"))
                .thenReturn(accountMock);

        when(accountMock.createCloudBlobClient()).thenReturn(blobClientMock);
        when(blobClientMock.getContainerReference("in")).thenReturn(containerInMock);
        when(blobClientMock.getContainerReference("out")).thenReturn(containerOutMock);

        when(containerInMock.exists()).thenReturn(true);
        when(containerOutMock.exists()).thenReturn(true);

        assertTrue(service.checkConnection());
    }

    @Test
    void shouldReturnFalseIfOneContainerDoesNotExist() throws Exception {
        HealthCheckService service = new HealthCheckService("fake", "in", "out");

        CloudStorageAccount accountMock = mock(CloudStorageAccount.class);
        CloudBlobClient blobClientMock = mock(CloudBlobClient.class);
        CloudBlobContainer containerInMock = mock(CloudBlobContainer.class);
        CloudBlobContainer containerOutMock = mock(CloudBlobContainer.class);

        mockedStatic = mockStatic(CloudStorageAccount.class);
        mockedStatic.when(() -> CloudStorageAccount.parse("fake"))
                .thenReturn(accountMock);

        when(accountMock.createCloudBlobClient()).thenReturn(blobClientMock);
        when(blobClientMock.getContainerReference("in")).thenReturn(containerInMock);
        when(blobClientMock.getContainerReference("out")).thenReturn(containerOutMock);

        when(containerInMock.exists()).thenReturn(true);
        when(containerOutMock.exists()).thenReturn(false);

        assertFalse(service.checkConnection());
    }

    @Test
    void shouldPropagateExceptionWhenParseFails() {
        HealthCheckService service = new HealthCheckService("invalid", "in", "out");

        mockedStatic = mockStatic(CloudStorageAccount.class);
        mockedStatic.when(() -> CloudStorageAccount.parse("invalid"))
                .thenThrow(new IllegalArgumentException("invalid connection"));

        assertThrows(IllegalArgumentException.class, service::checkConnection);
    }
}