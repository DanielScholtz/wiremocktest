package wiremock.response_template;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wiremock.WiremockTrainingTest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class WiremockResponseTemplate extends WiremockTrainingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WiremockResponseTemplate.class);
    protected static final String FILE_PATH = "response/__files/";

    protected WireMockServer wireMockServer;

    @Before
    public void setup() {
        initializeRestAssuredHttp();
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @After
    public void tearDown() {
        wireMockServer.stop();

        final List<LoggedRequest> theUnmatchedRequests = wireMockServer.findAllUnmatchedRequests();
        if (!theUnmatchedRequests.isEmpty()) {
            LOGGER.error("Unmatched requests: {}", theUnmatchedRequests);
        }
    }

    @Test
    public void allTypeOfResponseTransform() {
        wireMockServer.stop();

        Response response;
        final ResponseTemplateTransformer theTemplateTransformer =
                new ResponseTemplateTransformer(false);
        final String theTemplateTransformerName = theTemplateTransformer.getName();
        wireMockServer = new WireMockServer(
                WireMockConfiguration
                        .options()
                        .port(PORT)
                        .extensions(theTemplateTransformer));
        wireMockServer.start();

        wireMockServer.stubFor(
                get(urlPathEqualTo("/transform/"))
                        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withBodyFile(FILE_PATH + "transform_my_life.json")
                                .withTransformers(theTemplateTransformerName)
                                .withStatus(HttpStatus.OK.value())
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        )
        );

        final Response theResponse = given()
                .config(RestAssured.config().jsonConfig(JsonConfig.jsonConfig().with()))
                .contentType(ContentType.JSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(BASIC_PATH + "/transform/");

        theResponse
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON);

        logResponseStatusHeadersAndBody(theResponse);
    }


}
