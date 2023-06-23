package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.User;

public interface RatingRepository extends CrudRepository<Rating, Long> {
	
	public List<Rating> findAllByOrderByDateDesc();
	public List<Rating> findByProfessional(Professional professional);
	public List<Rating> findByWriter(User writer);
}
