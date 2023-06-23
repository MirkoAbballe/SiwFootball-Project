package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.User;


public interface UserRepository extends CrudRepository<User, Long> 
{
	public List<User> findByMyTeams(Team team);
	public List<User> findByMyProfessionals(Professional professional);
}
