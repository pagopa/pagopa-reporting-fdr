package it.gov.pagopa.fdr.functions;

import it.gov.pagopa.fdr.models.OptionsMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OptionsMessageTest {

    @Test
    void optionMessageTest() {

        OptionsMessage optionsMessage = new OptionsMessage();
        optionsMessage.setIdentificativoFlusso("IdentificativoFlusso");

        optionsMessage.setIdentificativoPSP("IdentificativoPSP");
        optionsMessage.setIdentificativoIntermediarioPSP("1");
        optionsMessage.setIdentificativoCanale("2");
        optionsMessage.setIdentificativoDominio("3");
        optionsMessage.setIdentificativoFlusso("4");
        optionsMessage.setDataOraFlusso("5");

        optionsMessage.setIdentificativoUnivocoRegolamento("6");
        optionsMessage.setDataRegolamento("7");
        optionsMessage.setIndiceDatiSingoloPagamento("8");
        optionsMessage.setIdentificativoUnivocoVersamento("9");
        optionsMessage.setIdentificativoUnivocoRiscossione("10");
        optionsMessage.setSingoloImportoPagato("11");
        optionsMessage.setCodiceEsitoSingoloPagamento("12");
        optionsMessage.setDataEsitoSingoloPagamento("13");

        assertNotNull(optionsMessage.getIdentificativoPSP());
        assertNotNull(optionsMessage.getDataOraFlusso());
    }

}