package it.gov.pagopa.fdr.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OptionsReportingModel {

  //    private String idFlow;
  //    private String dateFlow;
  //    private List<String> notificationCodes;
  private String indiceDatiSingoloPagamento;
  private String identificativoUnivocoVersamento;
  private String identificativoUnivocoRiscossione;
  private String singoloImportoPagato;
  private String codiceEsitoSingoloPagamento;
  private String dataEsitoSingoloPagamento;
}
