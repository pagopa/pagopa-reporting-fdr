package it.gov.pagopa.fdr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import it.gov.pagopa.fdr.models.OptionsMessage;
import it.gov.pagopa.fdr.models.OptionsReportingModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OptionsService {

    private String storageConnectionString;
    private Logger logger;
    private int optionsForMessage = 1;

    public OptionsService(String storageConnectionString, Logger logger) {

        this.storageConnectionString = storageConnectionString;
        this.logger = logger;
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


        this.logger.log(Level.INFO, "[OptionsService] START options_2_ehub for flow " + identificativoFlusso );

        List<List<OptionsReportingModel>> partitionOptions = Lists.partition(options, optionsForMessage);

        OptionsMessage optionsMsg;
        List<String> messages = new ArrayList<>();
        for (List<OptionsReportingModel> partitionOption : partitionOptions) {
            optionsMsg = new OptionsMessage();
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
        ehubTx.publishEvents(messages);

        messages.stream().forEach(msg -> this.logger.log(Level.INFO, () -> "[OptionsService] sent message " + msg));

        this.logger.log(Level.INFO, "[OptionsService] END options_2_ehub ");
    }
}
