package it.gov.pagopa.fdr.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EhubSenderTest {

  @Mock private EventHubProducerClient producer;
  @InjectMocks private EhubSender sut;

  @Test
  void publishEventsSuccess() {
    EventDataBatch dataBatchMock = mock(EventDataBatch.class);

    doReturn(dataBatchMock).when(producer).createBatch();
    doReturn(true).when(dataBatchMock).tryAdd(any());
    doReturn(2).when(dataBatchMock).getCount();

    assertDoesNotThrow(() -> sut.publishEvents(Collections.singletonList("test")));

    verify(producer).send(dataBatchMock);
    verify(producer).close();
  }

  @Test
  void publishEventsFail() {
    EventDataBatch dataBatchMock = mock(EventDataBatch.class);

    doReturn(dataBatchMock, dataBatchMock).when(producer).createBatch();
    doReturn(false, false).when(dataBatchMock).tryAdd(any());

    List<String> list = Collections.singletonList("test");
    assertThrows(IllegalArgumentException.class, () -> sut.publishEvents(list));

    verify(producer).send(dataBatchMock);
    verify(producer, never()).close();
  }
}
