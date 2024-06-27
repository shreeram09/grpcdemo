package org.shreeram.v1.child.messaging;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.shreeram.v1.child.domain.Person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class PersonConsumer {
    @Inject
    private PersonProducer producer;

    @Incoming("persons-in")
    @Outgoing("persons-out2")
    // @Transactional
    public Message<Person> addPerson(Message<Person> msg){
        log.info("{0}",msg.getPayload());
        // producer.sendPayload(msg);
        return msg.withPayload(msg.getPayload());
    }
}
