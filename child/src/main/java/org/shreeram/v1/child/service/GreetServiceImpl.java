package org.shreeram.v1.child.service;
import io.smallrye.mutiny.Uni;
import org.shreeram.grpc.Greet;
import io.quarkus.grpc.GrpcService;
import org.shreeram.grpc.SimpleRequest;
import org.shreeram.grpc.SimpleResponse;

@GrpcService
public class GreetServiceImpl implements Greet {
    @Override
    public Uni<SimpleResponse> hello(SimpleRequest request) {
        return Uni
                .createFrom()
                .item(
                        ()->SimpleResponse
                                .newBuilder()
                                .setLength(
                                        request
                                                .getName()
                                                .length()
                                ).build());
    }
}
