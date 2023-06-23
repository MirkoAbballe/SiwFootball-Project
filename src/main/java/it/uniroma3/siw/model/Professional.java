package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Professional 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	private Long id;
	
	private String name;
	private String surname;
	
	
	@Basic(fetch = FetchType.LAZY)
	@Lob
	@Column(name = "image")
	private byte[] image;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private String nationality;
	
	private Boolean isCaptain;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="myProfessionals")
	private List<User>listUser;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="winningProfessionals")
	private List<Trophy>trophy;
	
	private float valutationasCoach;
	
	private float valutationasFootballer;
	
	private String height; 
	
	private String footedness;
	
	private String position;
	
	private Integer goals;
	
	
	@OneToOne
	private Team teamAsCoach; /*indica la squadra per cui allena*/
	
	@ManyToOne
	private Team teamAsFootballer; /*indica la squadra per cui gioca*/
	
	@OneToMany(mappedBy = "professional")
	private List<Rating>professionalsRatings;
	
	
	@Override
	public int hashCode() 
	{
		return Objects.hash(name, surname);
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
		Professional other = (Professional) obj;
		return Objects.equals(name, other.name) && Objects.equals(surname, other.surname) && Objects.equals(height, other.height);
	}

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

	public String getSurname() 
	{
		return surname;
	}

	public void setSurname(String surname) 
	{
		this.surname = surname;
	}

	public byte[] getImage() 
	{
		return image;
	}

	public void setImage(byte[] image) 
	{
		this.image = image;
	}

	public LocalDate getDateOfBirth() 
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) 
	{
		this.dateOfBirth = dateOfBirth;
	}

	public String getNationality() 
	{
		return nationality;
	}

	public void setNationality(String nationality) 
	{
		this.nationality = nationality;
	}

	public String getHeight() 
	{
		return height;
	}

	public void setHeight(String height) 
	{
		this.height = height;
	}

	public String getFootedness() 
	{
		return footedness;
	}

	public void setFootedness(String footedness) 
	{
		this.footedness = footedness;
	}

	public String getPosition() 
	{
		return position;
	}

	public void setPosition(String position) 
	{
		this.position = position;
	}

	public Integer getGoals() 
	{
		return goals;
	}

	public void setGoals(Integer goals) 
	{
		this.goals = goals;
	}

	public Team getTeamAsCoach() 
	{
		return teamAsCoach;
	}

	public void setTeamAsCoach(Team teamAsCoach) 
	{
		this.teamAsCoach = teamAsCoach;
	}

	public Team getTeamAsFootballer() 
	{
		return teamAsFootballer;
	}

	public void setTeamAsFootballer(Team teamAsFootballer) 
	{
		this.teamAsFootballer = teamAsFootballer;
	}

	public List<Rating> getProfessionalsRatings() 
	{
		return professionalsRatings;
	}

	public void setProfessionalsRatings(List<Rating> professionalsRatings) 
	{
		this.professionalsRatings = professionalsRatings;
	}
	

	public Boolean getIsCaptain() 
	{
		return isCaptain;
	}

	public void setIsCaptain(Boolean isCaptain) 
	{
		this.isCaptain = isCaptain;
	}

	public List<User> getListUser() 
	{
		return listUser;
	}

	public void setListUser(List<User> listUser) 
	{
		this.listUser = listUser;
	}

	public List<Trophy> getTrophy() 
	{
		return trophy;
	}

	public void setTrophy(List<Trophy> trophy) 
	{
		this.trophy = trophy;
	}

	public float getValutationasCoach() 
	{
		return valutationasCoach;
	}

	public void setValutationasCoach(float valutationasCoach) 
	{
		this.valutationasCoach = valutationasCoach;
	}

	public float getValutationasFootballer() 
	{
		return valutationasFootballer;
	}

	public void setValutationasFootballer(float valutationasFootballer) 
	{
		this.valutationasFootballer = valutationasFootballer;
	}
}