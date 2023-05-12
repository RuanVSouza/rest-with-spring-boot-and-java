package br.com.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.curso.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{

}
