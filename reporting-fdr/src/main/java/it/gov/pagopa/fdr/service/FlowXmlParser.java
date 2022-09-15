package it.gov.pagopa.fdr.service;


import it.gov.pagopa.fdr.models.OptionsReportingModel;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class FlowXmlParser extends DefaultHandler {
    private List<OptionsReportingModel> options = new ArrayList<>();
    private OptionsReportingModel option;

    private String identificativoUnivocoRegolamento;
    private String dataRegolamento;

    public List<OptionsReportingModel> getOptions() {
        return options;
    }

    public String getIdentificativoUnivocoRegolamento() {
        return identificativoUnivocoRegolamento;
    }

    public String getDataRegolamento() {
        return dataRegolamento;
    }

    private final StringBuilder currentValue = new StringBuilder();

    @Override
    public void startElement(
            String uri,
            String localName,
            String qName,
            Attributes attributes) {

        // reset the tag value
        currentValue.setLength(0);

        String regexTarget = "^(.*):";
        String qNameNoNameSpace = qName.replaceAll(regexTarget, "");
        if (qNameNoNameSpace.equalsIgnoreCase("datiSingoliPagamenti")) {
            option = new OptionsReportingModel();
        }

    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qName) {

        String regexTarget = "^(.*):";
        String qNameNoNameSpace = qName.replaceAll(regexTarget, "");
        // datiSingoliPagamenti
        if (qNameNoNameSpace.equalsIgnoreCase("identificativoUnivocoVersamento")) {
            option.setIdentificativoUnivocoVersamento(currentValue.toString());
        }

        if (qNameNoNameSpace.equalsIgnoreCase("identificativoUnivocoRiscossione")) {
            option.setIdentificativoUnivocoRiscossione(currentValue.toString());
        }

        if (qNameNoNameSpace.equalsIgnoreCase("indiceDatiSingoloPagamento")) {
            option.setIndiceDatiSingoloPagamento(currentValue.toString());
        }

        if (qNameNoNameSpace.equalsIgnoreCase("singoloImportoPagato")) {
            option.setSingoloImportoPagato(currentValue.toString());
        }

        if (qNameNoNameSpace.equalsIgnoreCase("codiceEsitoSingoloPagamento")) {
            option.setCodiceEsitoSingoloPagamento(currentValue.toString());
        }

        if (qNameNoNameSpace.equalsIgnoreCase("dataEsitoSingoloPagamento")) {
            option.setDataEsitoSingoloPagamento(currentValue.toString());
        }
        // add
        if (qNameNoNameSpace.equalsIgnoreCase("datiSingoliPagamenti")) {
            options.add(option);
        }


        // header
        if (qNameNoNameSpace.equalsIgnoreCase("dataRegolamento")) {
            dataRegolamento = currentValue.toString();
        }

        if (qNameNoNameSpace.equalsIgnoreCase("identificativoUnivocoRegolamento")) {
            identificativoUnivocoRegolamento = currentValue.toString();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {

        currentValue.append(ch, start, length);

    }

}