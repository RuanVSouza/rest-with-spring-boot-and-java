package br.com.curso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.curso.model.Book;

public interface BookRepository extends JpaRepository<Book, Long>{

}
