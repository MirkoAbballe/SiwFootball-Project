package it.uniroma3.siw.model;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Rating 
{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime date;
	
	private Integer techinalskill;
	private Integer physicalability;
	private Integer shootingability;
	private Integer dribblingskill;
	
	private Integer tacticalknowledge;
	private Integer communicationskill;
	private Integer motivationalskill;
	private Integer gameanalysis;
	
	
	@ManyToOne
	private Professional professional; 
	
	@ManyToOne
	private User writer;

	public Long getId() 
	{
		return id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	public LocalDateTime getDate() 
	{
		return date;
	}

	public void setDate(LocalDateTime date) 
	{
		this.date = date;
	}


	public Professional getProfessional() 
	{
		return professional;
	}

	public void setProfessional(Professional professional) 
	{
		this.professional = professional;
	}

	public User getWriter() 
	{
		return writer;
	}

	public void setWriter(User writer) 
	{
		this.writer = writer;
	}

	public Integer getTechinalskill() 
	{
		return techinalskill;
	}

	public void setTechinalskill(Integer techinalskill) 
	{
		this.techinalskill = techinalskill;
	}

	public Integer getPhysicalability() 
	{
		return physicalability;
	}

	public void setPhysicalability(Integer physicalability) 
	{
		this.physicalability = physicalability;
	}

	public Integer getShootingability() 
	{
		return shootingability;
	}

	public void setShootingability(Integer shootingability) 
	{
		this.shootingability = shootingability;
	}

	public Integer getDribblingskill() 
	{
		return dribblingskill;
	}

	public void setDribblingskill(Integer dribblingskill) 
	{
		this.dribblingskill = dribblingskill;
	}

	public Integer getTacticalknowledge() 
	{
		return tacticalknowledge;
	}

	public void setTacticalknowledge(Integer tacticalknowledge) 
	
	{
		this.tacticalknowledge = tacticalknowledge;
	}

	public Integer getCommunicationskill() 
	{
		return communicationskill;
	}

	public void setCommunicationskill(Integer communicationskill) 
	{
		this.communicationskill = communicationskill;
	}

	public Integer getMotivationalskill() 
	{
		return motivationalskill;
	}

	public void setMotivationalskill(Integer motivationalskill) 
	{
		this.motivationalskill = motivationalskill;
	}

	public Integer getGameanalysis() 
	{
		return gameanalysis;
	}

	public void setGameanalysis(Integer gameanalysis) 
	{
		this.gameanalysis = gameanalysis;
	}
}
