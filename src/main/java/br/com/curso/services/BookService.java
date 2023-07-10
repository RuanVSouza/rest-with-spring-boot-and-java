package br.com.curso.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.curso.controllers.BookController;
import br.com.curso.controllers.PersonController;
import br.com.curso.data.vo.v1.BookVO;
import br.com.curso.exceptions.RequiredObjectIsNullException;
import br.com.curso.exceptions.ResourceNotFoundException;
import br.com.curso.mapper.DozerMapper;
import br.com.curso.model.Book;
import br.com.curso.repositories.BookRepository;

@Service
public class BookService {

	private Logger logger = Logger.getLogger(BookService.class.getName());
	
	@Autowired
	BookRepository repository;
	
	@Autowired
	PagedResourcesAssembler<BookVO> assembler;

	public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) {
		
		logger.info("Finding all book");
		
		var bookPage = repository.findAll(pageable);
		var bookVosPage = bookPage.map(p -> DozerMapper.parseObject(p, BookVO.class));
		bookVosPage.map(p -> 
			p.add(linkTo(methodOn(PersonController.class)
					.findById(p.getId()))
					.withSelfRel()));
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(bookVosPage, link);
	}

	public BookVO findById(Long id){
		logger.info("finding one BookVO!");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}

	public BookVO create(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one book!");
		
		var entity =  DozerMapper.parseObject(book, Book.class);
		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}
	

	public BookVO update(BookVO book){
		if(book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Update one book!");

		var entity = repository.findById(book.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());

		var vo = DozerMapper.parseObject(repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}

	public void delete(Long id) {
		logger.info("delete one book!");

		Book entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}

	
}
