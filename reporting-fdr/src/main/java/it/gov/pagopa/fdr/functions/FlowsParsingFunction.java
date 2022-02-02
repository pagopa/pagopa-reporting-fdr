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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Azure Functions with Azure Blob trigger.
 */
public class FlowsParsingFunction {
    private final String storageConnectionString = System.getenv("FLOW_SA_CONNECTION_STRING");
    //private String optionsQueue = System.getenv("OPTIONS_QUEUE");

    /**
     * This function will be invoked when a new or updated blob is detected at the
     * specified path. The blob contents are provided as input to this function.
     */
    @FunctionName("FlowsParsingFunction")
    public void run(
            @BlobTrigger(name = "BlobXmlTrigger", path = "%FLOWS_XML_BLOB%/{name}", dataType = "binary", connection = "FLOW_SA_CONNECTION_STRING") byte[] content,
            @BindingName("name") String name, final ExecutionContext context) {

        Logger logger = context.getLogger();

        logger.log(Level.INFO, () -> "Blob Trigger function executed at: " + LocalDateTime.now() + " for blob " + name);

        String converted = new String(content, StandardCharsets.UTF_8);

        logger.log(Level.INFO, () -> converted);



        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            // to be compliant, completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            SAXParser saxParser = factory.newSAXParser();

            FlowXmlParser handler = new FlowXmlParser();
            saxParser.parse(new InputSource(new StringReader(converted)), handler);

            OptionsService optionsService = this.getOptionsServiceInstance(logger);

            // identificativoPSP##identificativoIntermediarioPSP##identificativoCanale##identificativoDominio##identificativoFlusso##dataOraFlusso.xml
            // AGID_01##97735020584##97735020584_03##77777777777##2022-01-24GID_01-S003035679##2022-01-24T00:30:49.xml
            String[] flowInfo = name.split("##");
            String identificativoPSP = flowInfo[0];
            String identificativoIntermediarioPSP = flowInfo[1];
            String identificativoCanale = flowInfo[2];
            String identificativoDominio = flowInfo[3];
            String identificativoFlusso = flowInfo[4];
            String dataOraFlusso = flowInfo[5].substring(0,flowInfo[5].length()-4); // remove extension file
            logger.log(Level.INFO, () -> "Processing flow PSP " + identificativoPSP + " flow " + identificativoFlusso + " with date " + dataOraFlusso);
            optionsService.optionsProcessing(handler.getIdentificativoUnivocoRegolamento(), handler.getDataRegolamento(), handler.getOptions(), identificativoPSP, identificativoIntermediarioPSP, identificativoCanale, identificativoDominio, identificativoFlusso, dataOraFlusso);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.log(Level.INFO, () -> "Processing flow exception: " + e.getMessage());
        }
    }

    public OptionsService getOptionsServiceInstance(Logger logger) {

        return new OptionsService(this.storageConnectionString, logger);
    }

}
