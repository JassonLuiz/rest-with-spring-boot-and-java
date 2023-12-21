package io.github.jassonluiz.restwithspringbootandjava.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.jassonluiz.restwithspringbootandjava.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

}
