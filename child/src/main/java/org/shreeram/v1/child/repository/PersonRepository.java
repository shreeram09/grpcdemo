package org.shreeram.v1.child.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.shreeram.v1.child.domain.Person;
@ApplicationScoped
public class PersonRepository implements PanacheRepository<Person> { }
