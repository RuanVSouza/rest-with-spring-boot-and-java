package br.com.curso.services;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.curso.data.vo.v1.PersonVO;
import br.com.curso.data.vo.v2.PersonVOV2;
import br.com.curso.exceptions.ResourceNotFoundExcepcion;
import br.com.curso.mapper.DozerMapper;
import br.com.curso.mapper.custom.PersonMapper;
import br.com.curso.model.Person;
import br.com.curso.repositories.PersonRepository;

@Service
public class PersonService {

	@Autowired
	PersonRepository repository;
	
	@Autowired
	PersonMapper mapper;

	private Logger logger = Logger.getLogger(PersonService.class.getName());

	public List<PersonVO> findAll() {
		logger.info("Finding all people");

		return DozerMapper.parseListObjects(repository.findAll(), PersonVO.class);
	}

	public PersonVO findById(Long id) {
		logger.info("finding one PersonVO!");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundExcepcion("No records found for this ID"));
		return DozerMapper.parseObject(entity, PersonVO.class);
	
	}

	public PersonVO create(PersonVO person) {
		logger.info("Creating one person!");
		
		var entity =  DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		
		return vo;
	}
	
	public PersonVOV2 createV2(PersonVOV2 person) {
		
		logger.info("Creating one person with v2");
		
		var entity =  mapper.convertVoToEntity(person);
		var vo = mapper.convertEntityToVo(repository.save(entity));
		
		return vo;
	}

	public PersonVO update(PersonVO person) {
		logger.info("Update one person!");

		var entity = repository.findById(person.getId())
				.orElseThrow(() -> new ResourceNotFoundExcepcion("No records found for this ID"));

		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());

		var vo = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
		return vo;
	}

	public void delete(Long id) {
		logger.info("delete one person!");

		Person entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundExcepcion("No records found for this ID"));
		repository.delete(entity);
	}

	
}
