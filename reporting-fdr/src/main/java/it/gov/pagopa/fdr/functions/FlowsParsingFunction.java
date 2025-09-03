package it.gov.pagopa.fdr.functions;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.BlobTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import it.gov.pagopa.fdr.service.FlowXmlParser;
import it.gov.pagopa.fdr.service.OptionsService;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Azure Functions with Azure Blob trigger.
 */
@Slf4j
public class FlowsParsingFunction {

    private final OptionsService optionsService;

    public FlowsParsingFunction(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    public FlowsParsingFunction() {
        this.optionsService = new OptionsService();
    }

    /**
     * This function will be invoked when a new or updated blob is detected at the
     * specified path. The blob contents are provided as input to this function.
     */
    @FunctionName("FlowsParsingFunction")
    public void run(
            @BlobTrigger(name = "BlobXmlTrigger", path = "%FLOWS_XML_BLOB%/{name}", dataType = "binary", connection = "FLOW_SA_CONNECTION_STRING") byte[] content,
            @BindingName("name") String name, final ExecutionContext context) {
        log.info("Blob Trigger function executed at: {} for blob {}", LocalDateTime.now(), name);

        String convertedStr = new String(content, StandardCharsets.UTF_8);
        String converted= new String(DatatypeConverter.parseBase64Binary(convertedStr));

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // to be compliant, completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            SAXParser saxParser = factory.newSAXParser();

            FlowXmlParser handler = new FlowXmlParser();
            saxParser.parse(new InputSource(new StringReader(converted)), handler);

            // identificativoPSP--identificativoIntermediarioPSP--identificativoCanale--identificativoDominio--identificativoFlusso--dataOraFlusso.xml
            // AGID_01--97735020584--97735020584_03--77777777777--2022-01-24GID_01-S003035679--2022-01-24T00:30:49.xml
            String[] flowInfo = name.split("--");
            String identificativoPSP = flowInfo[0];
            String identificativoIntermediarioPSP = flowInfo[1];
            String identificativoCanale = flowInfo[2];
            String identificativoDominio = flowInfo[3];
            String identificativoFlusso = flowInfo[4];
            String dataOraFlusso = flowInfo[5].substring(0,flowInfo[5].length()-4); // remove extension file
            log.info("Processing flow PSP {} flow {} with date {}", identificativoPSP, identificativoFlusso, dataOraFlusso);
            this.optionsService.optionsProcessing(handler.getIdentificativoUnivocoRegolamento(), handler.getDataRegolamento(), handler.getOptions(), identificativoPSP, identificativoIntermediarioPSP, identificativoCanale, identificativoDominio, identificativoFlusso, dataOraFlusso);

            this.optionsService.shift2OutFile(name, converted);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            log.error("Processing flow exception", e);
        }
    }
}
