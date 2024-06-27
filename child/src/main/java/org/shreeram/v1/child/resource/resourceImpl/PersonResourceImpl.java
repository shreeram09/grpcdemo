package org.shreeram.v1.child.resource.resourceImpl;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.shreeram.v1.child.messaging.PersonProducer;
import org.shreeram.v1.child.domain.Person;
import org.shreeram.v1.child.repository.PersonRepository;
import org.shreeram.v1.child.resource.PersonResource;

@ApplicationScoped
public class PersonResourceImpl implements PersonResource {

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PersonProducer producer;

    @Override
    public Response persons() {
        return Response.ok(personRepository.listAll()).type(MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response person(long id) {
        final Person found = personRepository.findById(id);
        if (found == null) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(found, MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response person(String name,int age) {
        final String query = "select p.* from person p where p.name=:name and p.age=:age";
        Parameters params = Parameters.with("name", name).and("age", age);
        PanacheQuery<Person> paramQuery = personRepository.find(query, params);
        final Person found = paramQuery.firstResult();
        if (found == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(found, MediaType.APPLICATION_JSON).build();
    }

    @Override
    @Transactional
    public Response create(Person person) {
        Response response=  this.person(person.getName(),person.getAge());
        if(response.getStatusInfo()!=Response.Status.NOT_FOUND) return response;
        personRepository.persistAndFlush(person);
        producer.sendMessageToKafka(Message.of(person));
        return Response.status(Response.Status.CREATED).entity(person).build();
    }

    @Override
    @Transactional
    public Response update(long id, Person person) {
        Response response=  this.person(id);
        if(response.getStatusInfo()!=Response.Status.OK) return response;
        Person found = response.readEntity(Person.class);
        found.setName(person.getName());
        found.setAge(person.getAge());
        personRepository.persistAndFlush(found);
        return Response.ok(found).build();
    }

    @Transactional
    @Override
    public Response delete(long id) {
        Response response=  this.person(id);
        if(response.getStatusInfo()!=Response.Status.OK) return response;
        final boolean isDeleted = personRepository.deleteById(id);
        return isDeleted ? Response.ok().build() : Response.notModified().build();
    }
}
