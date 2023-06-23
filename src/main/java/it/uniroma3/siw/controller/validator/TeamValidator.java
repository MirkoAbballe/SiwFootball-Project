package it.uniroma3.siw.controller.validator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.repository.TeamRepository;

@Component
public class TeamValidator implements Validator 
{
	
	@Autowired
	private TeamRepository teamRepository;
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		Team team= (Team)o;
		LocalDate today= LocalDate.now();
		if(!team.getName().isEmpty() && team.getFoundedDate() !=null && teamRepository.existsByNameAndFoundedDate(team.getName(),team.getFoundedDate()))
		{
			errors.reject("team.duplicate");
		}
		else
		{
			if(team.getName().isEmpty())
			{
				errors.rejectValue("name", "namenull");
			}
			if(team.getFoundedDate()==null)
			{
				errors.rejectValue("foundedDate", "foundedatenull");
			}
			else
			{
				if(team.getFoundedDate().getYear()<1800 || team.getFoundedDate().getYear()> java.time.Year.now().getValue())
				{
				errors.rejectValue("foundedDate","foundedateincorrect");
				}
				else
				{
					if(!team.getFoundedDate().isBefore(today))
					{
						errors.rejectValue("foundedDate","foundedateincorrect");
					}
				}
			}
			
			if( team.getNationality().isEmpty() )
			{
				errors.rejectValue("nationality", "nationalitynull");
			}
			if( team.getHometown().isEmpty() )
			{
				errors.rejectValue("hometown", "hometownnull");
			}
			if( team.getStadium().isEmpty() )
			{
				errors.rejectValue("stadium","stadiumnull");
			}
			if( team.getStadiumCapacity()==null )
			{
				errors.rejectValue("stadiumCapacity","stadiumCapacitynull");
			}
			else
			{
				if(team.getStadiumCapacity()>150000)
				{
					errors.rejectValue("stadiumCapacity","stadiumCapacityexceeded");
				}
			}
			if( team.getNumberOfPlayer()==null )
			{
				errors.rejectValue("numberOfPlayer","numberOfPlayernull");
			}
			if( team.getPresident().isEmpty() )
			{
				errors.rejectValue("president","presidentnull");
			}
		}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Team.class.equals(aClass);
	}
}

