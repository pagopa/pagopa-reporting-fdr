package it.gov.pagopa.fdr.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.fdr.service.HealthCheckService;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Azure Functions with Azure Http trigger.
 */
@Slf4j
public class Info {

	private final HealthCheckService healthCheckService;

    public Info(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

	public Info() {
		this.healthCheckService = new HealthCheckService();
	}

	@FunctionName("Info")
	public HttpResponseMessage run (
			@HttpTrigger(name = "InfoTrigger",
			methods = {HttpMethod.GET},
			route = "info",
			authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) {

		try {
			boolean isConnected = this.healthCheckService.checkConnection();
			log.info("Invoked health check HTTP trigger for pagopa-reporting-fdr.");

			if(!isConnected) throw new Exception("Health check connection error");

			return request.createResponseBuilder(HttpStatus.OK)
						   .header("Content-Type", "application/json")
						   .build();
		} catch (Exception e) {
			log.error("Health check error", e);

			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
						   .header("Content-Type", "application/json")
						   .build();
		}
	}
}
