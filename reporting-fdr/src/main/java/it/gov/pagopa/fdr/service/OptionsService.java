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
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OptionsService {

    private String storageConnectionString;
    private Logger logger;
    private int optionsForMessage = 1;
    private final String containerBlobOut;
    private final String containerBlobIn;

    public OptionsService(String storageConnectionString, Logger logger, String containerBlobOut, String containerBlobIn) {
        this.storageConnectionString = storageConnectionString;
        this.logger = logger;
        this.containerBlobIn = containerBlobIn;
        this.containerBlobOut = containerBlobOut;
    }

    public void optionsProcessing(String identificativoUnivocoRegolamento,
                                  String dataRegolamento,
                                  List<OptionsReportingModel> options,
                                  String identificativoPSP,
                                  String identificativoIntermediarioPSP,
                                  String identificativoCanale,
                                  String identificativoDominio,
                                  String identificativoFlusso,
                                  String dataOraFlusso) throws JsonProcessingException {


        this.logger.log(Level.INFO, "[OptionsService] START opt2ehub flow " + identificativoFlusso + " with " + options.size() + " flows");

        List<List<OptionsReportingModel>> partitionOptions = Lists.partition(options, optionsForMessage);

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
            optionsMsg.setIndiceDatiSingoloPagamento(partitionOption.get(0).getIndiceDatiSingoloPagamento());
            optionsMsg.setIdentificativoUnivocoVersamento(partitionOption.get(0).getIdentificativoUnivocoVersamento());
            optionsMsg.setIdentificativoUnivocoRiscossione(partitionOption.get(0).getIdentificativoUnivocoRiscossione());
            optionsMsg.setSingoloImportoPagato(partitionOption.get(0).getSingoloImportoPagato());
            optionsMsg.setCodiceEsitoSingoloPagamento(partitionOption.get(0).getCodiceEsitoSingoloPagamento());
            optionsMsg.setDataEsitoSingoloPagamento(partitionOption.get(0).getDataEsitoSingoloPagamento());

            messages.add(new ObjectMapper().writeValueAsString(optionsMsg));
        }

        this.logger.log(Level.INFO, () -> "[OptionsService] " + options.size() + " flows in " + partitionOptions.size()
                + "  batch of size " + optionsForMessage);

        EhubSender ehubTx = new EhubSender();
        ehubTx.publishEvents(messages, this.logger);

        this.logger.log(Level.INFO, "[OptionsService] END opt2ehub flow " + identificativoFlusso + " with " + options.size() + " flows");

    }


    public void shift2OutFile(String csvFileName, String content)
            throws FileNotFoundException {
        // insert blob in OUTPUT container
        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder().connectionString(this.storageConnectionString).buildClient();
        BlobContainerClient containerBlobOutClient =
                blobServiceClient.getBlobContainerClient(this.containerBlobOut);
        BlobClient blobClient = containerBlobOutClient.getBlobClient(csvFileName);
        blobClient.upload(BinaryData.fromString(content), true);

        logger.log(Level.INFO, () -> "[OptionsService] move [" + csvFileName + "] in output container");

        // delete blob in INPUT container
        BlobContainerClient containerBlobInClient =
                blobServiceClient.getBlobContainerClient(this.containerBlobIn);
        BlobClient blobInClient = containerBlobInClient.getBlobClient(csvFileName);
        blobInClient.delete();
        logger.log(Level.INFO, () -> "[OptionsService] delete [" + csvFileName + "] from in input container");
    }

}
