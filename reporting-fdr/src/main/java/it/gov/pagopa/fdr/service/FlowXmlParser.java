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

        if (qName.equalsIgnoreCase("datiSingoliPagamenti")) {
            option = new OptionsReportingModel();
        }

    }

    @Override
    public void endElement(String uri,
                           String localName,
                           String qName) {

        // datiSingoliPagamenti
        if (qName.equalsIgnoreCase("identificativoUnivocoVersamento")) {
            option.setIdentificativoUnivocoVersamento(currentValue.toString());
        }

        if (qName.equalsIgnoreCase("identificativoUnivocoRiscossione")) {
            option.setIdentificativoUnivocoRiscossione(currentValue.toString());
        }

        if (qName.equalsIgnoreCase("indiceDatiSingoloPagamento")) {
            option.setIndiceDatiSingoloPagamento(currentValue.toString());
        }

        if (qName.equalsIgnoreCase("singoloImportoPagato")) {
            option.setSingoloImportoPagato(currentValue.toString());
        }

        if (qName.equalsIgnoreCase("codiceEsitoSingoloPagamento")) {
            option.setCodiceEsitoSingoloPagamento(currentValue.toString());
        }

        if (qName.equalsIgnoreCase("dataEsitoSingoloPagamento")) {
            option.setDataEsitoSingoloPagamento(currentValue.toString());
        }
        // add
        if (qName.equalsIgnoreCase("datiSingoliPagamenti")) {
            options.add(option);
        }


        // header
        if (qName.equalsIgnoreCase("dataRegolamento")) {
            dataRegolamento = currentValue.toString();
        }

        if (qName.equalsIgnoreCase("identificativoUnivocoRegolamento")) {
            identificativoUnivocoRegolamento = currentValue.toString();
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {

        currentValue.append(ch, start, length);

    }

}