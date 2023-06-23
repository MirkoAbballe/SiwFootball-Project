package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Trophy;

@Component
public class TrophyUpdateValidator implements Validator 
{
	
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		Trophy trophy= (Trophy)o;
		
		if(trophy.getVenue().isEmpty())
		{
			errors.rejectValue("venue","venuenull");
		}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Trophy.class.equals(aClass);
	}
}