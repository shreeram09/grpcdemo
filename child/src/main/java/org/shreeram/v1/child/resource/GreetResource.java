package org.shreeram.v1.child.resource;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.shreeram.grpc.Greet;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.shreeram.grpc.SimpleRequest;
import org.shreeram.grpc.SimpleResponse;

@Path("/")
public class GreetResource {
    @GrpcClient
    Greet client;

    @GET
    @Path("hello/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<String> helloMutiny(@PathParam("name") String name) {
        return Uni.createFrom().item(
                ()->"name is "
                        +client.hello(SimpleRequest.newBuilder().setName(name).build())
                .onItem()
                .transform(SimpleResponse::getLength)
                        +" characters long");
    }
}
