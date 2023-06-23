package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)

	private Long id;
	@NotNull
	@NotBlank
	@Size(max = 255)
	private String name;
	
	@NotNull
	private Integer numberOfPlayer;

	@NotNull
	private String stadium;
	
	@Lob
	@Column(name = "image")
	private byte[] image;

	@OneToOne
	private Professional professionalasCoach;
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy="teamAsFootballer")
	private List<Professional> professionalasFootballer;
	
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy="winningTeam")
	private List<Trophy>trophy;
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy="myTeams")
	private List<User>listUser;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate foundedDate;

	@NotNull
	private String hometown;
	
	@NotNull
	private String nationality;
	
	@NotNull
	private Integer stadiumCapacity;
	
	@NotNull
	private String president;

	private Boolean isOnTopDivision = false;

	
	@Override
	public int hashCode() 
	{
		return Objects.hash(name,foundedDate);
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
		Team other = (Team) obj;
		return Objects.equals(name, other.name) && Objects.equals(foundedDate, other.foundedDate);
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


	public String getStadium() 
	{
		return stadium;
	}

	public void setStadium(String stadium) 
	{
		this.stadium = stadium;
	}

	public byte[] getImage() 
	{
		return image;
	}

	public void setImage(byte[] image) 
	{
		this.image = image;
	}

	public Integer getStadiumCapacity() 
	{
		return stadiumCapacity;
	}

	public void setStadiumCapacity(Integer stadiumCapacity) 
	{
		this.stadiumCapacity = stadiumCapacity;
	}

	public String getPresident() 
	{
		return president;
	}

	public void setPresident(String president) 
	{
		this.president = president;
	}

	public Boolean getIsOnTopDivision() 
	{
		return isOnTopDivision;
	}

	public void setIsOnTopDivision(Boolean isOnTopDivision) 
	{
		this.isOnTopDivision = isOnTopDivision;
	}

	public String getNationality() 
	{
		return nationality;
	}

	public void setNationality(String nationality) 
	{
		this.nationality = nationality;
	}

	public String getHometown() 
	{
		return hometown;
	}

	public void setHometown(String hometown) 
	{
		this.hometown = hometown;
	}

	public List<Trophy> getTrophy() 
	{
		return trophy;
	}

	public void setTrophy(List<Trophy> trophy) 
	{
		this.trophy = trophy;
	}
	
	public List<User> getListUser() 
	{
		return listUser;
	}

	public void setListUser(List<User> listUser) 
	{
		this.listUser = listUser;
	}

	public LocalDate getFoundedDate() 
	{
		return foundedDate;
	}

	public void setFoundedDate(LocalDate foundedDate) 
	{
		this.foundedDate = foundedDate;
	}

	public Integer getNumberOfPlayer() 
	{
		return numberOfPlayer;
	}

	public void setNumberOfPlayer(Integer numberOfPlayer) 
	{
		this.numberOfPlayer = numberOfPlayer;
	}

	public Professional getProfessionalasCoach() {
		return professionalasCoach;
	}

	public void setProfessionalasCoach(Professional professionalasCoach) {
		this.professionalasCoach = professionalasCoach;
	}

	public List<Professional> getProfessionalasFootballer() {
		return professionalasFootballer;
	}

	public void setProfessionalasFootballer(List<Professional> professionalasFootballer) {
		this.professionalasFootballer = professionalasFootballer;
	}
}

