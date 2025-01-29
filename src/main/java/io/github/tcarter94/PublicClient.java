package io.github.tcarter94;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.pnc.api.dto.ComponentVersion;
import org.jboss.pnc.api.konfluxbuilddriver.dto.BuildRequest;
import org.jboss.pnc.api.konfluxbuilddriver.dto.BuildResponse;
import org.jboss.pnc.api.konfluxbuilddriver.dto.CancelRequest;

@RegisterRestClient(configKey = "public")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PublicClient {

    @POST
    @Path("/build")
    BuildResponse build(BuildRequest buildRequest);

    @PUT
    @Path("/cancel")
    Response cancel(CancelRequest cancelRequest);

    @Path("/version")
    @GET
    ComponentVersion getVersion();
}
