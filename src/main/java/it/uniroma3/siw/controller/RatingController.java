package it.uniroma3.siw.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import it.uniroma3.siw.controller.validator.RatingValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.repository.ProfessionalRepository;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.RatingRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class RatingController {
	@Autowired RatingRepository ratingRepository;
	@Autowired TeamRepository teamRepository;
	@Autowired ProfessionalRepository professionalRepository;
	@Autowired RatingValidator ratingValidator;
	@Autowired private CredentialsService credentialsService;


	@GetMapping("/admin/Ratings")
	public String Ratings(Model model)
	{
		model.addAttribute("ratings",this.ratingRepository.findAll());
		
		return "admin/Ratings";
	}
	
	
	@Transactional
	@GetMapping("/admin/deleteRating/{id}")
	public String deleteRating(@PathVariable("id") Long id, Model model)
	{
		
		Rating rating = this.ratingRepository.findById(id).get();
		
		Professional professional= rating.getProfessional();
		
		professional.getProfessionalsRatings().remove(rating);
		
        this.ratingRepository.delete(rating);
        
        float valutazione=0;
    	
    	List<Rating> RatingsProfessional=this.ratingRepository.findByProfessional(professional);
    	
    	if(RatingsProfessional.size()==0)
    	{
    		professional.setValutationasCoach(0);
    		professional.setValutationasFootballer(0);
    	}
    	
    	for(Rating ratingProfess: RatingsProfessional)
    	{
    		if(!ratingProfess.getProfessional().getPosition().equals("Allenatore")) 
    		{
    			valutazione=valutazione+(float)
		    			(ratingProfess.getTechinalskill()+ratingProfess.getPhysicalability()
		    			+ratingProfess.getShootingability()+ratingProfess.getDribblingskill())/4;
    		}
    		else 
    		{
    			valutazione=valutazione+(float)(ratingProfess.getTacticalknowledge()+ratingProfess.getCommunicationskill()
    			+ratingProfess.getMotivationalskill()+ratingProfess.getGameanalysis())/4;
    		}
    		
    		float val= (float)(valutazione/RatingsProfessional.size());
    		if(professional.getPosition().equals("Allenatore"))
    		{
    			professional.setValutationasCoach(val);
    		}
    		else
    		{
    			professional.setValutationasFootballer(val);
    		}
    	}
    	
    	this.professionalRepository.save(professional);
    		
    	model.addAttribute("ratings", this.ratingRepository.findAll());
		
		return "admin/Ratings";
	}
	
	
	@GetMapping("/default/Newrating/{id}")
	public String newRating(Model model,@PathVariable("id") Long id)
	{
		Professional professional=this.professionalRepository.findById(id).get();
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	List<Rating>ratings= this.ratingRepository.findByProfessional(professional);
    	
    	for(Rating rating: ratings)
    	{
    		if( rating.getWriter().getId() == credentials.getUser().getId() ) 
    		{
    			return "index";
    		}
    	}
    		
		model.addAttribute("rating", new Rating());
		model.addAttribute("professional",this.professionalRepository.findById(id).get());
		
		return "default/newRatingProfessional";
	}
	
	
	@GetMapping("/default/myRatings")
	public String myRatings(Model model)
	{
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
		model.addAttribute("ratings", this.ratingRepository.findByWriter(credentials.getUser()));
		
		return "default/myRatings";
	}
	
	
	@PostMapping("/default/setRating/{id}")
	public String newRating(@Valid @ModelAttribute("rating") Rating rating,BindingResult bindingResult,
	                       Model model,@PathVariable("id") Long id)
	{
		
		Professional professional= this.professionalRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	
	    this.ratingValidator.validate(rating, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		this.professionalRepository.save(professional);
	    		
	    		
	    		rating.setWriter(credentials.getUser());
	    		rating.setProfessional(professional);
	    		rating.setDate(LocalDateTime.now());
	    		
	    		this.ratingRepository.save(rating);
	    		
	    		
	    		float valutazione=0;
		    	
		    	List<Rating> AllRatingProfessional=this.ratingRepository.findByProfessional(professional);
		    	
		    	for(Rating ratingProfess: AllRatingProfessional)
		    	{
		    		if(!ratingProfess.getProfessional().getPosition().equals("Allenatore"))
		    		{
		    			valutazione=valutazione+(float)
		    			(ratingProfess.getTechinalskill()+ratingProfess.getPhysicalability()
		    			+ratingProfess.getShootingability()+ratingProfess.getDribblingskill())/4;
		    		}
		    		else
		    		{
		    			valutazione=valutazione+(float)(ratingProfess.getTacticalknowledge()+ratingProfess.getCommunicationskill()
		    			+ratingProfess.getMotivationalskill()+ratingProfess.getGameanalysis())/4;
		    		}
		    	}
		    	
		    	float val= (float)(valutazione/AllRatingProfessional.size());
		    	
	    		if(professional.getPosition().equals("Allenatore"))
	    		{
	    			professional.setValutationasCoach(val);
	    		}
	    		else
	    		{
	    		professional.setValutationasFootballer(val);
	    		}
	    		
	    		this.professionalRepository.save(professional);
	    		
		    	model.addAttribute("professional",professional);
		    	model.addAttribute("rating",rating);
		    	return "public/professional";
	    	}
	    	else
	    	{
	    		model.addAttribute("professional",professional);
	    		return "default/newRatingProfessional";
	    	}	
		}
	}