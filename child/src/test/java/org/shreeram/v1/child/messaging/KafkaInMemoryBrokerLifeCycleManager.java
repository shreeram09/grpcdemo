package org.shreeram.v1.child.messaging;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

public class KafkaInMemoryBrokerLifeCycleManager implements QuarkusTestResourceLifecycleManager {
    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> channelIn = InMemoryConnector.switchIncomingChannelsToInMemory("persons-in");
        Map<String, String> channelOut = InMemoryConnector.switchOutgoingChannelsToInMemory("persons-out");
        Map<String, String> channelOut2 = InMemoryConnector.switchOutgoingChannelsToInMemory("persons-out2");
        env.putAll(channelIn);
        env.putAll(channelOut);
        env.putAll(channelOut2);
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
