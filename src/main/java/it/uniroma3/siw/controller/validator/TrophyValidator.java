package it.uniroma3.siw.controller.validator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Trophy;
import it.uniroma3.siw.repository.TrophyRepository;

@Component
public class TrophyValidator implements Validator 
{
	
	@Autowired
	private TrophyRepository trophyRepository;
	
	@Override 
	public void validate(Object o, Errors errors) 
	{
		Trophy trophy= (Trophy)o;
		LocalDate today=LocalDate.now();
		
		if(!trophy.getName().isEmpty() && trophy.getDate()!=null && this.trophyRepository.existsByNameAndDate(trophy.getName(), trophy.getDate()))
		{
			errors.reject("trophy.duplicate");
		}
		else
		{
			if(trophy.getName().isEmpty()) 
			{
				errors.rejectValue("name", "namenull");
			}
			if(trophy.getDate()==null)
			{
				errors.rejectValue("date", "datenull");
			}
			else 
			{
				if(trophy.getDate().getYear()<1800)
				{
					errors.rejectValue("date","dateincorrect");
				}
				if(!trophy.getDate().isBefore(today))
				{
					errors.rejectValue("date", "dateincorrect");
				}
			}
			if(trophy.getVenue().isEmpty())
			{
				errors.rejectValue("venue","venuenull");
			}
		}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Trophy.class.equals(aClass);
	}
}