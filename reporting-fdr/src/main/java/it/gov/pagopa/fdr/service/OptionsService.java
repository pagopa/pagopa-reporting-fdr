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

        messages.stream().forEach(msg -> {
                this.logger.log(Level.INFO, () -> "[OptionsService] sent message " + msg);
        });

//        try {
//            CloudQueue queue = CloudStorageAccount.parse(storageConnectionString).createCloudQueueClient()
//                    .getQueueReference(this.optionsQueue);
//            queue.createIfNotExists();
//            this.logger.log(Level.INFO, () -> "[OptionsService] Sending messages ");
//
//            messages.stream().forEach(msg -> {
//                try {
//                    this.logger.log(Level.INFO, () -> "[OptionsService] sent message " + msg);
//                    queue.addMessage(new CloudQueueMessage(msg));
//                } catch (StorageException e) {
//                    logger.log(Level.INFO, () -> "[OptionsService] sent exception : " + e.getMessage());
//                }
//            });
//
//        } catch (URISyntaxException | StorageException | InvalidKeyException e) {
//            this.logger.log(Level.INFO, () -> "[OptionsService] queue exception : " + e.getMessage());
//        }

        this.logger.log(Level.INFO, "[OptionsService] END options_2_ehub ");
    }

//    public void callPaymentServiceToReportOption(OptionsMessage options) {
//
//        this.logger.log(Level.INFO, () -> "[OptionsService]  call PaymentService to report the options related to flow "
//                + options.getIdFlow());
//
//        BooleanResponseModel response = ClientBuilder.newClient()
//                .target(this.paymentHost + "/payments/options/reporting").request()
//                .post(Entity.entity(this.getOptionsReportingModel(options), MediaType.APPLICATION_JSON),
//                        BooleanResponseModel.class);
//
//        if (Boolean.FALSE.equals(response.getResult())) {
//
//            throw new IllegalArgumentException("Option reporting error for flow " + options.getIdFlow());
//        }
//
//        this.logger.log(Level.INFO, "[OptionsService] options reported");
//    }

//    public OptionsReportingModel getOptionsReportingModel(OptionsMessage optionsMessage) {
//
//        List<String> notificationCodes = Arrays.asList(optionsMessage.getIuvs()).stream()
//                .map(iuv -> this.auxDigit + iuv).collect(Collectors.toList());
//
//        OptionsReportingModel optionsReportingRequest = new OptionsReportingModel();
//        optionsReportingRequest.setIdFlow(optionsMessage.getIdFlow());
//        optionsReportingRequest.setDateFlow(optionsMessage.getDateFlow());
//        optionsReportingRequest.setNotificationCodes(notificationCodes);
//
//        return optionsReportingRequest;
//    }
}
