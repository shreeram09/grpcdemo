package org.shreeram.v1.child.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.shreeram.v1.child.domain.Person;

@Path("/PersonData")
public interface PersonResource {
    @GET
    @Path("/persons")
    @Produces(MediaType.APPLICATION_JSON)
    Response persons();

    @GET
    @Path("/persons/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Response person(@PathParam("id") long id);

    @GET
    @Path("/persons?name={name}&age={age}")
    @Produces(MediaType.APPLICATION_JSON)
    Response person(@QueryParam("name") String name, @QueryParam("age") int age);

    @POST
    @Path("/persons")
    @Consumes(MediaType.APPLICATION_JSON)
    Response create(Person person);

    @PUT
    @Path("/persons/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    Response update(@PathParam("id") long id,Person person);

    @DELETE
    @Path("/persons/{id}")
    Response delete(@PathParam("id") long id);
}
