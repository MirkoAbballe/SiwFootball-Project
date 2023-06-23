package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Team;

@Component
public class RatingValidator implements Validator 
{

	
	@Override 
	public void validate(Object o, Errors errors) 
	{
	
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Team.class.equals(aClass);
	}
}
