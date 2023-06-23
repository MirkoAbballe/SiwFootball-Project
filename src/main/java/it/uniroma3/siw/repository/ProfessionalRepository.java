package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;


public interface ProfessionalRepository extends CrudRepository<Professional, Long> 
{
	
	public boolean existsByNameAndSurnameAndHeight(String name, String surname, String height);
	public List<Professional> findByName(String name);
	public List<Professional> findByNationality(String nationality);
	public List<Professional> findByPosition(String position);
	public List<Professional> findByTeamAsFootballer(Team team);
	public Professional findByTeamAsCoach(Team team);
	public List<Professional> findAllByOrderByValutationasFootballerDesc();
	public List<Professional> findAllByOrderByValutationasCoachDesc();
	public List<Professional> findByNameAndNationality(String name,String nationality);
	public List<Professional> findByNameAndPosition(String name,String position);
	public List<Professional> findByNationalityAndPosition(String nationality,String position);
	public List<Professional> findByNameAndNationalityAndPosition(String name,String nationality,String position);
}