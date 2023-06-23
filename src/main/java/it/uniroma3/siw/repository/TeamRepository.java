package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long>
{

	public boolean existsByNameAndFoundedDate(String name, LocalDate foundedDate);
	public List<Team> findByName(String name);
	public List<Team> findByStadium(String stadium);
	public List<Team> findByNationality(String nationaliry);
	public List<Team> findByNameAndStadium(String name,String stadium);
	public List<Team> findByNameAndNationality(String name,String nationality);
	public List<Team> findByStadiumAndNationality(String stadium,String nationality);
	public List<Team> findByNameAndStadiumAndNationality(String name,String stadium,String nationality);
	public boolean existsByName(String name);
	
}