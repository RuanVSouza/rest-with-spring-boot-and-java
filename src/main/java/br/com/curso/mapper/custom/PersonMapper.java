package br.com.curso.mapper.custom;

import org.springframework.stereotype.Service;

import br.com.curso.data.vo.v1.PersonVO;
import br.com.curso.model.Person;

@Service
public class PersonMapper {

	public PersonVO convertEntityToVo(Person person) {
		PersonVO vo = new PersonVO();
		vo.setId(person.getId());
		vo.setAddress(person.getAddress());
		//vo.setBirthDay(new Date());	
		vo.setFirstName(person.getFirstName());
		vo.setLastName(person.getLastName());
		vo.setGender(person.getGender());
		return vo;
	
	}
	public Person convertVoToEntity(PersonVO person) {
		Person entity = new Person();
		entity.setId(person.getId());
		entity.setAddress(person.getAddress());
		//vo.setBirthDay(new Date());	
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setGender(person.getGender());
		return entity;
		
	}
	
}
