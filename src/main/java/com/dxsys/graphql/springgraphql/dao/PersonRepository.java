package com.dxsys.graphql.springgraphql.dao;

import com.dxsys.graphql.springgraphql.entity.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person,Integer> {
    Person findByEmail(String email);
}
