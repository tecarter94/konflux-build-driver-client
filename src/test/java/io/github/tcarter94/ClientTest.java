package io.github.tcarter94;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.ws.rs.core.MediaType;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.pnc.api.constants.HttpHeaders;
import org.jboss.pnc.api.dto.ComponentVersion;
import org.jboss.pnc.api.dto.Request;
import org.jboss.pnc.api.konfluxbuilddriver.dto.BuildRequest;
import org.jboss.pnc.api.konfluxbuilddriver.dto.BuildResponse;
import org.jboss.pnc.api.konfluxbuilddriver.dto.CancelRequest;
import org.jboss.pnc.api.konfluxbuilddriver.dto.PipelineNotification;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.JsonBody;

import io.quarkiverse.mockserver.test.InjectMockServerClient;
import io.quarkiverse.mockserver.test.MockServerTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@QuarkusTestResource(MockServerTestResource.class)
@TestProfile(ClientTestProfile.class)
public class ClientTest {

    @InjectMockServerClient
    MockServerClient mockServerClient;

    @Inject
    @RestClient
    InternalClient internalClient;

    @Inject
    @RestClient
    PublicClient publicClient;

    static final String NAMESPACE = "foo";
    static final String PIPELINE_ID = "bar";

    @Test
    public void buildExecutionCompletedTest() throws URISyntaxException {
        final Request request = Request.builder()
                .method(Request.Method.PUT)
                .header(new Request.Header(HttpHeaders.CONTENT_TYPE_STRING, MediaType.APPLICATION_JSON))
                .attachment(null)
                .uri(new URI("foo.bar"))
                .build();
        final PipelineNotification pipelineNotification = PipelineNotification.builder().completionCallback(request)
                .buildId("1234")
                .status("Succeeded").build();

        // Create mock rest endpoint
        mockServerClient
                .when(request()
                        .withPath("/internal/completed")
                        .withBody(JsonBody.json(pipelineNotification))
                        .withMethod(Request.Method.PUT.toString()))
                .respond(httpRequest -> response()
                        .withStatusCode(HttpStatusCode.OK_200.code()));

        final Response response = internalClient.buildExecutionCompleted(pipelineNotification);
        assertEquals(HttpStatusCode.OK_200.code(), response.getStatus());
    }

    @Test
    public void buildTest() {
        final BuildRequest buildRequest = BuildRequest.builder().namespace(NAMESPACE).podMemoryOverride("1Gi").build();
        final BuildResponse buildResponse = BuildResponse.builder().namespace(NAMESPACE).pipelineId(PIPELINE_ID).build();

        // Create mock rest endpoint
        mockServerClient
                .when(request()
                        .withPath("/build")
                        .withBody(JsonBody.json(buildRequest))
                        .withMethod(Request.Method.POST.toString()))
                .respond(httpRequest -> response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(JsonBody.json(buildResponse)));

        assertEquals(buildResponse, publicClient.build(buildRequest));
    }

    @Test
    public void cancelTest() {
        final CancelRequest cancelRequest = CancelRequest.builder().namespace(NAMESPACE).pipelineId(PIPELINE_ID).build();

        // Create mock rest endpoint
        mockServerClient
                .when(request()
                        .withPath("/cancel")
                        .withBody(JsonBody.json(cancelRequest))
                        .withMethod(Request.Method.PUT.toString()))
                .respond(httpRequest -> response()
                        .withStatusCode(HttpStatusCode.OK_200.code()));

        final Response response = publicClient.cancel(cancelRequest);
        assertEquals(HttpStatusCode.OK_200.code(), response.getStatus());
    }

    @Test
    public void versionTest() {
        final ComponentVersion componentVersion = ComponentVersion.builder().name("konflux-build-driver")
                .builtOn(ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS))
                .commit("foo")
                .version("bar")
                .build();

        // Create mock rest endpoint
        mockServerClient
                .when(request()
                        .withPath("/version")
                        .withMethod(Request.Method.GET.toString()))
                .respond(httpRequest -> response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(JsonBody.json(componentVersion)));

        assertEquals(componentVersion, publicClient.getVersion());
    }
}