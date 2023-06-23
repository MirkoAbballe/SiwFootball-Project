package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.repository.ProfessionalRepository;


@Component
public class ProfessionalValidator implements Validator
{

	@Autowired
	private ProfessionalRepository professionalRepository;
	
	
	@Override
	public void validate(Object o, Errors errors) 
	{
		Professional professional= (Professional)o;
		LocalDate today= LocalDate.now();
		
			if(!professional.getName().isEmpty() && !professional.getSurname().isEmpty() && !professional.getHeight().isEmpty() 
					&& this.professionalRepository.existsByNameAndSurnameAndHeight(professional.getName(), 
							professional.getSurname(),professional.getHeight()))
			{
				errors.reject("professional.duplicate");
			}
		else
		{
			if(professional.getName().isEmpty())
			{ 
				errors.rejectValue("name", "namenull");
			}
			if(professional.getSurname().isEmpty())
			{
				errors.rejectValue("surname", "surnamenull");
			}
			if(professional.getDateOfBirth()==null)
			{
				errors.rejectValue("dateOfBirth", "dateOfBirthnull");
			}
			else
			{
				if(professional.getDateOfBirth().getYear()<1900 || professional.getDateOfBirth().getYear()> java.time.Year.now().getValue())
				{
				errors.rejectValue("dateOfBirth","dateOfBirthincorrect");
				}
				else
				{
					if(!professional.getDateOfBirth().isBefore(today))
					{
						errors.rejectValue("dateOfBirth","dateOfBirthincorrect");
					}
				}
			}
			if( professional.getNationality().isEmpty() )
			{
				errors.rejectValue("nationality", "nationalitynull");
			}
			if(professional.getHeight()==null)
			{
				errors.rejectValue("height", "heightnull");
			}
			if(professional.getGoals()==null)
			{
				errors.rejectValue("goals", "goalsnull");
			}
			if(professional.getFootedness().isEmpty())
			{
				errors.rejectValue("footedness","footednessnull");
			}
			if(professional.getPosition().isEmpty())
			{
				errors.rejectValue("position","positionnull");
			}
		}
	}
	
	@Override
	public boolean supports(Class<?> aClass) 
	{
		return Professional.class.equals(aClass);
	}
}
