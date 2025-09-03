package it.gov.pagopa.fdr.service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import it.gov.pagopa.fdr.models.OptionsMessage;
import it.gov.pagopa.fdr.models.OptionsReportingModel;
import it.gov.pagopa.fdr.util.Util;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OptionsService {

  private final String storageConnectionString = System.getenv("FLOW_SA_CONNECTION_STRING");
  private final String containerBlobOut = System.getenv("OUTPUT_BLOB");
  private final String containerBlobIn = System.getenv("FLOWS_XML_BLOB");
  private int optionsForMessage = 1;

  private final EhubSender ehubTx;

  public OptionsService(EhubSender ehubTx) {
    this.ehubTx = ehubTx;
  }

  public OptionsService() {
    this.ehubTx = new EhubSender();
  }

  public void optionsProcessing(
      String identificativoUnivocoRegolamento,
      String dataRegolamento,
      List<OptionsReportingModel> options,
      String identificativoPSP,
      String identificativoIntermediarioPSP,
      String identificativoCanale,
      String identificativoDominio,
      String identificativoFlusso,
      String dataOraFlusso)
      throws JsonProcessingException {
    log.info(
        "[OptionsService] START opt2ehub flow {}} with {} flows",
        identificativoFlusso,
        options.size());

    List<List<OptionsReportingModel>> partitionOptions =
        Lists.partition(options, optionsForMessage);

    OptionsMessage optionsMsg;
    List<String> messages = new ArrayList<>();
    for (List<OptionsReportingModel> partitionOption : partitionOptions) {
      optionsMsg = new OptionsMessage();

      // id + version
      optionsMsg.setVersion("v1");
      optionsMsg.setId(Util.generateType1UUID().toString());

      // common header
      optionsMsg.setIdentificativoPSP(identificativoPSP);
      optionsMsg.setIdentificativoIntermediarioPSP(identificativoIntermediarioPSP);
      optionsMsg.setIdentificativoCanale(identificativoCanale);
      optionsMsg.setIdentificativoDominio(identificativoDominio);
      optionsMsg.setIdentificativoFlusso(identificativoFlusso);
      optionsMsg.setDataOraFlusso(dataOraFlusso);
      // FlussoRiversamento hd
      optionsMsg.setIdentificativoUnivocoRegolamento(identificativoUnivocoRegolamento);
      optionsMsg.setDataRegolamento(dataRegolamento);
      // datiSingoliPagamenti
      optionsMsg.setIndiceDatiSingoloPagamento(
          partitionOption.get(0).getIndiceDatiSingoloPagamento());
      optionsMsg.setIdentificativoUnivocoVersamento(
          partitionOption.get(0).getIdentificativoUnivocoVersamento());
      optionsMsg.setIdentificativoUnivocoRiscossione(
          partitionOption.get(0).getIdentificativoUnivocoRiscossione());
      optionsMsg.setSingoloImportoPagato(partitionOption.get(0).getSingoloImportoPagato());
      optionsMsg.setCodiceEsitoSingoloPagamento(
          partitionOption.get(0).getCodiceEsitoSingoloPagamento());
      optionsMsg.setDataEsitoSingoloPagamento(
          partitionOption.get(0).getDataEsitoSingoloPagamento());

      messages.add(new ObjectMapper().writeValueAsString(optionsMsg));
    }

    log.info(
        "[OptionsService] {} flows in {} batch of size {}",
        options.size(),
        partitionOptions.size(),
        optionsForMessage);

    this.ehubTx.publishEvents(messages);

    log.info(
        "[OptionsService] END opt2ehub flow {} with {} flows",
        identificativoFlusso,
        options.size());
  }

  public void shift2OutFile(String csvFileName, String content) throws FileNotFoundException {
    // insert blob in OUTPUT container
    BlobServiceClient blobServiceClient =
        new BlobServiceClientBuilder().connectionString(this.storageConnectionString).buildClient();
    BlobContainerClient containerBlobOutClient =
        blobServiceClient.getBlobContainerClient(this.containerBlobOut);
    BlobClient blobClient = containerBlobOutClient.getBlobClient(csvFileName);
    blobClient.upload(BinaryData.fromString(content), true);

    log.info("[OptionsService] move {} in output container", csvFileName);

    // delete blob in INPUT container
    BlobContainerClient containerBlobInClient =
        blobServiceClient.getBlobContainerClient(this.containerBlobIn);
    BlobClient blobInClient = containerBlobInClient.getBlobClient(csvFileName);
    blobInClient.delete();
    log.info("[OptionsService] delete {} from in input container", csvFileName);
  }
}
