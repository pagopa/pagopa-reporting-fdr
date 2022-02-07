package it.gov.pagopa.fdr.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionsReportingModel {

  private String indiceDatiSingoloPagamento;
  private String identificativoUnivocoVersamento;
  private String identificativoUnivocoRiscossione;
  private String singoloImportoPagato;
  private String codiceEsitoSingoloPagamento;
  private String dataEsitoSingoloPagamento;
}
