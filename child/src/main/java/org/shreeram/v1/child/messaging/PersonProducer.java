package org.shreeram.v1.child.messaging;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.shreeram.v1.child.domain.Person;

import java.util.Optional;

@ApplicationScoped
@Slf4j
public class PersonProducer {
    @Channel("persons-out")
    private Emitter<Person> emitter;

    @Channel("persons-out2")
    private Emitter<Person> emitter2;

    public void sendMessageToKafka(Message<Person> person){
         emitter.send(person);
    }
    public Message<Person> addPerson(Message<Person> msg){
        Optional<IncomingKafkaRecordMetadata> metadata= msg.getMetadata(IncomingKafkaRecordMetadata.class);
        metadata.ifPresent(metaInf-> log.info("{0} {1} {2} ",metaInf.getTopic(),metaInf.getKey(),metaInf.getChannel()));
        return msg;
    }

    public Message<Person> sendPayload(Message<Person> msg){
        Optional<IncomingKafkaRecordMetadata> metadata= msg.getMetadata(IncomingKafkaRecordMetadata.class);
        metadata.ifPresent(metaInf-> log.info("{0} {1} {2} ",metaInf.getTopic(),metaInf.getKey(),metaInf.getChannel()));
        emitter2.send(msg.getPayload());
        return msg;
    }
}
