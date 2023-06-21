package it.gov.pagopa.fdr.functions;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import it.gov.pagopa.fdr.service.HealthCheckService;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Azure Functions with Azure Http trigger.
 */
public class Info {

	/**
	 * This function will be invoked when a Http Trigger occurs
	 * @return
	 */
	@FunctionName("Info")
	public HttpResponseMessage run (
			@HttpTrigger(name = "InfoTrigger",
			methods = {HttpMethod.GET},
			route = "info",
			authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
			final ExecutionContext context) {

		Logger logger = context.getLogger();
		HealthCheckService healthCheckService = new HealthCheckService();

		try {
			boolean isConnected = healthCheckService.checkConnection();
			logger.log(Level.INFO, "Invoked health check HTTP trigger for pagopa-reporting-fdr.");

			if(!isConnected) throw new Exception("Health check connection error");

			return request.createResponseBuilder(HttpStatus.OK)
						   .header("Content-Type", "application/json")
						   .build();
		} catch (Exception e) {
			logger.log(Level.SEVERE, () -> "Health check error: " + e.getLocalizedMessage());

			return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
						   .header("Content-Type", "application/json")
						   .build();
		}
	}
}
