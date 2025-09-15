package it.gov.pagopa.fdr.functions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import it.gov.pagopa.fdr.service.HealthCheckService;
import it.gov.pagopa.fdr.util.HttpResponseMessageMock;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class InfoTest {

  @Mock private HealthCheckService healthCheckService;
  @Mock private ExecutionContext executionContext;
  @Mock private HttpRequestMessage<Optional<String>> request;
  @InjectMocks private Info sut;

  @Test
  @SneakyThrows
  void infoTestSuccess() {
    doAnswer(
            (Answer<HttpResponseMessage.Builder>)
                invocation -> {
                  HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                  return new HttpResponseMessageMock.HttpResponseMessageBuilderMock()
                      .status(status);
                })
        .when(request)
        .createResponseBuilder(any(HttpStatus.class));
    doReturn(true).when(healthCheckService).checkConnection();

    HttpResponseMessage response = assertDoesNotThrow(() -> sut.run(request, executionContext));

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  @SneakyThrows
  void infoTestFail() {
    doAnswer(
            (Answer<HttpResponseMessage.Builder>)
                invocation -> {
                  HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                  return new HttpResponseMessageMock.HttpResponseMessageBuilderMock()
                      .status(status);
                })
        .when(request)
        .createResponseBuilder(any(HttpStatus.class));
    doReturn(false).when(healthCheckService).checkConnection();

    HttpResponseMessage response = assertDoesNotThrow(() -> sut.run(request, executionContext));

    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatus());
  }
}
