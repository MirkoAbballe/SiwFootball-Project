package it.uniroma3.siw.model;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Trophy 
{

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	
	private String venue;
	
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	
	@ManyToOne
	private Team winningTeam;
	
	@ManyToMany
	private List<Professional>winningProfessionals;
	
	@Lob
	@Column(name = "image")
	private byte[] image;
	
	@Lob
	@Column(name = "imageWin")
	private byte[] imageWin;

	public Long getId() 
	{
		return id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getVenue() 
	{
		return venue;
	}

	public void setVenue(String venue) 
	{
		this.venue = venue;
	}

	public LocalDate getDate() 
	{
		return date;
	}

	public void setDate(LocalDate date) 
	{
		this.date = date;
	}

	public Team getWinningTeam() 
	{
		return winningTeam;
	}

	public void setWinningTeam(Team winningTeam) 
	{
		this.winningTeam = winningTeam;
	}

	public byte[] getImage() 
	{
		return image;
	}

	public void setImage(byte[] image) 
	{
		this.image = image;
	}
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(name,date);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		Trophy other = (Trophy) obj;
		return Objects.equals(name, other.name) && Objects.equals(date, other.date);
	}

	public byte[] getImageWin() 
	{
		return imageWin;
	}

	public void setImageWin(byte[] imageWin) 
	{
		this.imageWin = imageWin;
	}

	public List<Professional> getWinningProfessionals() 
	{
		return winningProfessionals;
	}

	public void setWinningProfessionals(List<Professional> winningProfessionals) 
	{
		this.winningProfessionals = winningProfessionals;
	}
}
	
	
	
	