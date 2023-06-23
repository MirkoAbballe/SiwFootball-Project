package it.uniroma3.siw.controller.validator;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Team;

@Component
public class TeamUpdateValidator implements Validator 
{
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		Team team= (Team)o;
		
		if( team.getNumberOfPlayer()==null )
		{
			errors.rejectValue("numberOfPlayer","numberOfPlayernull");
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
		if( team.getPresident().isEmpty() )
		{
			errors.rejectValue("president","presidentnull");
		}
	}
	
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Team.class.equals(aClass);
	}
}
