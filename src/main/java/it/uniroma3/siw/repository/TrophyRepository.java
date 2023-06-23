package it.uniroma3.siw.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Trophy;

public interface TrophyRepository extends CrudRepository<Trophy, Long> 
{
	public boolean existsByNameAndDate(String name,LocalDate date);
	public List<Trophy> findByWinningTeam(Team team); 
	public List<Trophy> findByWinningProfessionals(Professional professional);
}