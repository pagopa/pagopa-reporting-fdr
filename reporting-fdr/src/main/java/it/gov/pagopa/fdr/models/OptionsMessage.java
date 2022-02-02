package it.gov.pagopa.fdr.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionsMessage {

  private String identificativoPSP;
  private String identificativoIntermediarioPSP;
  private String identificativoCanale;
  private String identificativoDominio;
  private String identificativoFlusso;
  private String dataOraFlusso;

  private String identificativoUnivocoRegolamento;
  private String dataRegolamento;
  private String indiceDatiSingoloPagamento;
  private String identificativoUnivocoVersamento;
  private String identificativoUnivocoRiscossione;
  private String singoloImportoPagato;
  private String codiceEsitoSingoloPagamento;
  private String dataEsitoSingoloPagamento;
}
