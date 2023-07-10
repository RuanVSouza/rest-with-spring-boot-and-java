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

import br.com.curso.controllers.PersonController;
import br.com.curso.data.vo.v1.PersonVO;
import br.com.curso.exceptions.RequiredObjectIsNullException;
import br.com.curso.exceptions.ResourceNotFoundException;
import br.com.curso.mapper.DozerMapper;
import br.com.curso.model.Person;
import br.com.curso.repositories.PersonRepository;
import jakarta.transaction.Transactional;

@Service
public class PersonService {

	@Autowired
	PersonRepository repository;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;
	
	private Logger logger = Logger.getLogger(PersonService.class.getName());

	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable) {
		
		logger.info("Finding all people");
		
		var personPage = repository.findAll(pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p -> 
			p.add(linkTo(methodOn(PersonController.class)
					.findById(p.getId()))
					.withSelfRel()));
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personVosPage, link);
	}
	
	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstname, Pageable pageable) {
		
		logger.info("Finding all people");
		
		var personPage = repository.findPersonsByName(firstname,pageable);
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p -> 
		p.add(linkTo(methodOn(PersonController.class)
				.findById(p.getId()))
				.withSelfRel()));
		
		Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO findById(Long id){
		logger.info("finding one PersonVO!");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}

	public PersonVO create(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one person!");
		
		var entity =  DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}
	
	

	public PersonVO update(PersonVO person){
		if(person == null) throw new RequiredObjectIsNullException();
		
		logger.info("Update one person!");

		var entity = repository.findById(person.getId())
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}
	
	@Transactional
	public PersonVO disablePerson(Long id){
		logger.info("Disabling one PersonVO!");
		
		 repository.disablePerson(id);
		
		var entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getId())).withSelfRel());
		return vo;
	}

	public void delete(Long id) {
		logger.info("delete one person!");

		Person entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No records found for this ID"));
		repository.delete(entity);
	}

	
}
