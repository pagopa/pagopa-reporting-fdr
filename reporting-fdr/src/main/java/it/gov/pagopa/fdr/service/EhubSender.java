package it.gov.pagopa.fdr.service;

import com.azure.messaging.eventhubs.*;
import com.google.common.collect.Lists;
import com.microsoft.azure.storage.StorageException;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EhubSender {
  // Event Hubs namespace connection string
  private static final String connectionString = System.getenv("EHUB_FDR_CONNECTION_STRING");
  // Event hub name
  private static final String eventHubName = System.getenv("EHUB_FDR_NAME");

  /**
   * Code sample for publishing events.
   * @throws IllegalArgumentException if the EventData is bigger than the max batch size.
   */
  public void publishEvents(List<String> messages, Logger logger) {
    // create a producer client
    EventHubProducerClient producer = new EventHubClientBuilder()
            .connectionString(connectionString, eventHubName)
            .buildProducerClient();

    // events in an array
    List<EventData> allEvents = messages.stream().map( e -> new EventData(e)).collect(Collectors.toList());

    // ----- start
//    int batchSizeDebtPosTable = 100;
//    List<List<DebtPositionEntity>> partitionDebtPositionEntities = Lists.partition(this.getDebtPositionEntities(fileName, payments), batchSizeDebtPosTable);
//
//    // save debt positions partition in table
//    IntStream.range(0, partitionDebtPositionEntities.size()).forEach(partitionAddIndex -> {
//      try {
//        List<DebtPositionEntity> partitionBlock = partitionDebtPositionEntities.get(partitionAddIndex);
//        this.addDebtPositionEntityList(partitionBlock);
//        logger.log(Level.INFO, () -> "[CuCsvService] Azure Table Storage - Add for partition index " + partitionAddIndex + " executed.");
//        savedDebtPositionEntities.addAll(partitionBlock);
//      } catch (InvalidKeyException | URISyntaxException | StorageException e) {
//        logger.log(Level.SEVERE, () -> "[CuCsvService] Exception in add Azure Table Storage batch debt position entities: " + e.getMessage() + " " + e.getCause());
//      }
//    });
    // ------ stop

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
          logger.log(Level.SEVERE, () -> "[DebtPositionTableService] Error#1 " + eventData);
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
