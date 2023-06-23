package it.uniroma3.siw.model;


import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "users")

public class User 
{
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String name;
	private String surname;
	private String username;
		
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Team> myTeams;
	
	@ManyToMany(fetch = FetchType.LAZY)
	private List<Professional> myProfessionals;
	
	@OneToMany(mappedBy="writer")
	private List<Rating> myRatings; 
	
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
	
	public LocalDate getDateOfBirth() 
	{
		return dateOfBirth;
	}
	
	public void setDateOfBirth(LocalDate dateOfBirth) 
	{
		this.dateOfBirth = dateOfBirth;
	}
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Rating> getMyRatings() {
		return myRatings;
	}

	public void setMyRatings(List<Rating> myRatings) {
		this.myRatings = myRatings;
	}

	public List<Team> getMyTeams() {
		return myTeams;
	}

	public void setMyTeams(List<Team> myTeams) {
		this.myTeams = myTeams;
	}

	public List<Professional> getMyProfessionals() {
		return myProfessionals;
	}

	public void setMyProfessionals(List<Professional> myProfessionals) {
		this.myProfessionals = myProfessionals;
	}
}
