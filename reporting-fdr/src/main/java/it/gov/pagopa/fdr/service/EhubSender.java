package it.gov.pagopa.fdr.service;

import com.azure.messaging.eventhubs.*;
import java.util.List;
import java.util.stream.Collectors;

public class EhubSender {
  // Event Hubs namespace connection string
  private static final String connectionString = System.getenv("EHUB_FDR_CONNECTION_STRING");
  // Event hub name
  private static final String eventHubName = System.getenv("EHUB_FDR_NAME");

  /**
   * Code sample for publishing events.
   * @throws IllegalArgumentException if the EventData is bigger than the max batch size.
   */
  public void publishEvents(List<String> messages) {
    // create a producer client
    EventHubProducerClient producer = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();

    // events in an array
    List<EventData> allEvents = messages.stream().map( e -> new EventData(e)).collect(Collectors.toList());
    // create a batch
    EventDataBatch eventDataBatch = producer.createBatch();

    for (EventData eventData : allEvents) {
      // try to add the event from the array to the batch
      if (!eventDataBatch.tryAdd(eventData)) {
        // if the batch is full, send it and then create a new batch
        producer.send(eventDataBatch);
        eventDataBatch = producer.createBatch();

        // Try to add that event that couldn't fit before.
        if (!eventDataBatch.tryAdd(eventData)) {
          throw new IllegalArgumentException("Event is too large for an empty batch. Max size: "
                  + eventDataBatch.getMaxSizeInBytes());
        }
      }
    }
    // send the last batch of remaining events
    if (eventDataBatch.getCount() > 0) {
      producer.send(eventDataBatch);
    }
    producer.close();
  }
}
