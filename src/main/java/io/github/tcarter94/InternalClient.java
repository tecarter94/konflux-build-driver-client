package io.github.tcarter94;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.pnc.api.konfluxbuilddriver.dto.PipelineNotification;

@RegisterRestClient(configKey = "internal")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface InternalClient {

    @PUT
    @Path("/completed")
    Response buildExecutionCompleted(PipelineNotification notification);
}
