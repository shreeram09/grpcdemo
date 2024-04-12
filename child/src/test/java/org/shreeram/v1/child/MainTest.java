package org.shreeram.v1.child;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.shreeram.grpc.Greet;
import org.shreeram.grpc.SimpleRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
class MainTest {
    @GrpcClient
    Greet client;
    @Test
    void testGrpcCall() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Long> message=new CompletableFuture<>();
        client.hello(SimpleRequest.newBuilder().setName("radhesham").build())
                .subscribe().with(reply->
                        message.complete(reply.getLength()));
        assertTrue(message.get(5, TimeUnit.SECONDS).equals(9L));
    }

}