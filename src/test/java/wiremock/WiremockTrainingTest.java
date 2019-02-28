package wiremock;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WiremockTrainingTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WiremockTrainingTest.class);

    protected static final int PORT = 8081;
    protected static final String BASIC_PATH = "http://localhost:" + PORT;


    protected void initializeRestAssuredHttp() {
        RestAssured.reset();
        RestAssured.port = PORT;
    }

    protected void logResponseStatusHeadersAndBody(final Response inResponseEntity) {
        LOGGER.info("Response status: {}", inResponseEntity.getStatusCode());
        LOGGER.info("Response headers: {}", inResponseEntity.getHeaders());
        LOGGER.info("Response body: " + inResponseEntity.getBody().asString());
    }
}
