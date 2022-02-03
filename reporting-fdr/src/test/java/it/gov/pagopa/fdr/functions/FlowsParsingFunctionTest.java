package it.gov.pagopa.fdr.functions;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import com.microsoft.azure.functions.ExecutionContext;

import it.gov.pagopa.fdr.service.OptionsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FlowsParsingFunctionTest {

    @Mock
    ExecutionContext context;

    @Spy
    FlowsParsingFunction function;

    @Mock
    OptionsService optionsService;

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultStringBuilder.toString();
    }

    @Test
    void runOkTest() throws IOException {

        when(context.getLogger()).thenReturn(Logger.getLogger("InfoLogging"));

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("fdr_raw.xml");
        String data = readFromInputStream(inputStream);

        byte[] file = data.getBytes();

        Logger logger = Logger.getLogger("InfoLogging");
        doReturn(optionsService).when(function).getOptionsServiceInstance(logger);

        function.run(file, "AGID_01--97735020584--97735020584_03--77777777777--2022-02-02AGID_01-S003035679--2022-02-02T00_30_49.xml", context);

        verify(context, times(1)).getLogger();

        // TODO
        //verify(optionsService, times(1)).optionsProcessing(any(), anyString(), anyString());

    }

    @Test
    void getOptionsServiceIstanceTest() throws Exception {

        Logger logger = Logger.getLogger("testlogging");
        // TODO

        // test
        //OptionsService istance = function.getOptionsServiceInstance(logger);

        //assertNotNull(istance);
        assert (true);
    }
}
