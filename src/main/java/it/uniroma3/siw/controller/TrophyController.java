package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import it.uniroma3.siw.controller.validator.TrophyUpdateValidator;
import it.uniroma3.siw.controller.validator.TrophyValidator;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Trophy;
import it.uniroma3.siw.repository.ProfessionalRepository;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TrophyRepository;

@Controller
public class TrophyController 
{
	@Autowired TrophyRepository trophyRepository;
	@Autowired ProfessionalRepository professionalRepository;
	@Autowired TeamRepository teamRepository;
	@Autowired TrophyValidator trophyValidator;
	@Autowired TrophyUpdateValidator trophyUpdateValidator;
	
	 
	
	@InitBinder
	public void initBinder(WebDataBinder binder) 
	{
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	
	@GetMapping("/admin/formNewTrophy")
	public String formNewTrophy(Model model) 
	{
		model.addAttribute("trophy", new Trophy());
		return "admin/formNewTrophy";	
	}
	
	
	@GetMapping("/public/trophies/{id}")
	public String getTrophy(@PathVariable("id") Long id, Model model) 
	{
		
		Trophy trophy= this.trophyRepository.findById(id).get();
	
		model.addAttribute("trophy",trophy);
		
	    return "public/trophy";
	}
	
	
	
	@PostMapping("/admin/trophies")
	public String newTrophy(@Valid @ModelAttribute("trophy") Trophy trophy,
	                       BindingResult bindingResult,
	                       Model model,
	                       @RequestParam("image") MultipartFile imageFile)throws IOException
	   {

	    	this.trophyValidator.validate(trophy, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		byte[] imageBytes = imageFile.getBytes();
	    		trophy.setImage(imageBytes);
	    		this.trophyRepository.save(trophy);
	    		model.addAttribute("trophy", trophy);
	    		return "public/trophy";
	    	}
	    	else 
	    	{
	    		return "admin/formNewTrophy";
	    	}
	    }
	
	@GetMapping("/admin/formUpdateTrophy/{id}")
	public String getUpdateTrophy(@PathVariable("id") Long id, Model model) 
	{
		
		Trophy trophy=this.trophyRepository.findById(id).get();
		
		model.addAttribute("trophy",trophy);
        
		return "admin/formUpdateTrophy";
	}
	
	
	@GetMapping("/admin/manageTrophies")
	public String managetrophies(Model model) 
	{
		model.addAttribute("trophies", this.trophyRepository.findAll());
		return "admin/manageTrophies";
		
	}
	
	@Transactional
	@GetMapping("/admin/addTeamToTrophy/{id}")
	public String addTeamToTrophy(@PathVariable("id") Long id, Model model) 
	{

	    Trophy trophy = this.trophyRepository.findById(id).get();
	    Team team = trophy.getWinningTeam();

	    List<Team> teamsAvailable = (List<Team>) this.teamRepository.findAll();

	    Iterator<Team> iterator = teamsAvailable.iterator();
	    while (iterator.hasNext()) 
	    {
	        Team currentTeam = iterator.next();
	        if (team != null && currentTeam.getId().equals(team.getId())) {
	            iterator.remove();
	        }
	    }

	    model.addAttribute("trophy", trophy);
	    model.addAttribute("teams", teamsAvailable);

	    return "admin/teamsToAdd";
	}
	
	@Transactional
	@GetMapping("/admin/setTeamToTrophy/{teamid}/{trophyid}")
	public String setTrophyToTeam(@PathVariable("teamid") Long teamid, @PathVariable("trophyid") Long trophyid,Model model) 
	{ 
		
		Team team= this.teamRepository.findById(teamid).get();
		Trophy trophy= this.trophyRepository.findById(trophyid).get();
		
		List<Team>teamsAvailable= (List<Team>) this.teamRepository.findAll();
		
			
		if(trophy.getWinningTeam()==null)
		{
			team.getTrophy().add(trophy);
			this.teamRepository.save(team);
			
			trophy.setWinningTeam(team);
			this.trophyRepository.save(trophy);
		}
			
		else
			/*se il trofeo ha già una squadra vincitrice si deve prima rimuovere..*/
		{
			Team teamRemoveTrophy= trophy.getWinningTeam();
			teamRemoveTrophy.getTrophy().remove(trophy);
			
			team.getTrophy().add(trophy);
			this.teamRepository.save(team);
			
			trophy.setWinningTeam(team);
			this.trophyRepository.save(trophy);
		}
		
		Iterator<Team> iterator = teamsAvailable.iterator();
		   while (iterator.hasNext()) 
		   {
		       Team currentTeam = iterator.next();
		       if (team != null && currentTeam.getId().equals(team.getId())) 
		       {
		           iterator.remove();
		       }
		   }
		
		model.addAttribute("teams",teamsAvailable);
		model.addAttribute("trophy",trophy);	
		
		return "admin/teamsToAdd";
	}
	
	@Transactional
	@GetMapping("/admin/updateProfessionalsOfTrophy/{id}")
	public String updateProfessionalOfTrophy(@PathVariable("id") Long id, Model model) 
	{
	    Trophy trophy = this.trophyRepository.findById(id).get();

	    List<Professional> professionalsAvailable = (List<Professional>) this.professionalRepository.findAll();

	    Iterator<Professional> iterator = professionalsAvailable.iterator();
	    while (iterator.hasNext()) 
	    {
	        Professional professionalAvail = iterator.next();
	        for (Trophy Trophy : professionalAvail.getTrophy()) 
	        {
	            if (trophy.getId().equals(Trophy.getId())) 
	            {
	                iterator.remove();
	            }
	        }
	    }
	    
	    Iterator<Professional> availIterator = trophy.getWinningProfessionals().iterator();
	    while (availIterator.hasNext()) 
	    {
	        Professional professional = availIterator.next();
	        if (professional.getPosition().equals("Allenatore")) 
	        {
	            Iterator<Professional> availIteratorCopy = professionalsAvailable.iterator();
	            while (availIteratorCopy.hasNext()) 
	            {
	                Professional professionalAvail = availIteratorCopy.next();
	                if (professionalAvail.getPosition().equals("Allenatore")) 
	                {
	                    availIteratorCopy.remove();
	                }
	            }
	        }
	    }
	    
	    model.addAttribute("professionalAvail", professionalsAvailable);
	    model.addAttribute("trophy", trophy);
	    return "admin/professionalsToAddTrophy";
	}

	
	
	@GetMapping("/admin/setProfessionalToTrophy/{professionalid}/{trophyid}")
	public String setProfessionalToTrophy(@PathVariable("professionalid") Long professionalid, @PathVariable("trophyid") Long trophyid,Model model) 
	{ 
		
		Trophy trophy= this.trophyRepository.findById(trophyid).get();
		Professional professional= this.professionalRepository.findById(professionalid).get();
		List<Professional>ProfessionalsAvailable= (List<Professional>) this.professionalRepository.findAll();
		
		
			trophy.getWinningProfessionals().add(professional);
			this.trophyRepository.save(trophy);
			
			professional.getTrophy().add(trophy);
			this.professionalRepository.save(professional);
			
		
			Iterator<Professional> iterator = ProfessionalsAvailable.iterator();
	        while (iterator.hasNext()) 
	        {
	            Professional professionalAvail = iterator.next();
	            for (Trophy Trophy : professionalAvail.getTrophy()) 
	            {
	                if (trophy.getId().equals(Trophy.getId())) 
	                {
	                    iterator.remove();
	                    this.professionalRepository.save(professionalAvail);
	                }
	            }
	        }
	        
	        Iterator<Professional> Iterator = trophy.getWinningProfessionals().iterator();
	        while (Iterator.hasNext()) 
	        {
	            Professional Professional = Iterator.next();
	            if (Professional.getPosition().equals("Allenatore")) 
	            {
	                Iterator<Professional> availIterator = ProfessionalsAvailable.iterator();
	                while (availIterator.hasNext()) 
	                {
	                    Professional professionalAvail = availIterator.next();
	                    if (professionalAvail.getPosition().equals("Allenatore")) 
	                    {
	                        availIterator.remove();
	                    }
	                }
	            }
	        }
		
	        model.addAttribute("professionalAvail", ProfessionalsAvailable);
		    model.addAttribute("trophy", trophy);
		    return "admin/professionalsToAddTrophy";
	}
	
	
	@GetMapping("/admin/removeProfessionalFromTrophy/{professionalid}/{trophyid}")
	public String removeProfessionalFromTrophy(@PathVariable("professionalid") Long professionalid,@PathVariable("trophyid")Long trophyid, Model model) 
	{
		
	        Professional professional= this.professionalRepository.findById(professionalid).get();
	        Trophy trophy = this.trophyRepository.findById(trophyid).get();
	        
	        
	        trophy.getWinningProfessionals().remove(professional);
	        this.trophyRepository.save(trophy);
	        
	        professional.getTrophy().remove(trophy);
	        this.professionalRepository.save(professional);

	       
	        List<Professional>ProfessionalsAvailable= (List<Professional>) this.professionalRepository.findAll();
			
			
	        Iterator<Professional> iterator = ProfessionalsAvailable.iterator();
	        while (iterator.hasNext()) 
	        {
	            Professional professionalAvail = iterator.next();
	            for (Trophy Trophy : professionalAvail.getTrophy()) 
	            {
	                if (trophy.getId().equals(Trophy.getId())) 
	                {
	                    iterator.remove();
	                    this.professionalRepository.save(professionalAvail);
	                }
	            }
	        }
	        
	        Iterator<Professional> Iterator = trophy.getWinningProfessionals().iterator();
	        while (Iterator.hasNext()) 
	        {
	            Professional Professional = Iterator.next();
	            if (Professional.getPosition().equals("Allenatore")) 
	            {
	                Iterator<Professional> availIterator = ProfessionalsAvailable.iterator();
	                while (availIterator.hasNext()) 
	                {
	                    Professional professionalAvail = availIterator.next();
	                    if (professionalAvail.getPosition().equals("Allenatore")) 
	                    {
	                        availIterator.remove();
	                    }
	                }
	            }
	        }

	        model.addAttribute("trophy",trophy);
	        model.addAttribute("professionalAvail",ProfessionalsAvailable);
	        
	        return "admin/professionalsToAddTrophy";
	 }

	
	@PostMapping("/admin/trophy/edit/{id}")
	public String editTrophy(@Valid @ModelAttribute("trophy") Trophy trophy,BindingResult bindingResult,
			@PathVariable("id") Long id,Model model,@RequestParam("image") MultipartFile imageTrophyFile,
			@RequestParam("name") String name,@RequestParam("venue")String venue, 
			@RequestParam("imageWin") MultipartFile imageWin) 
			throws IOException
	{
		
		  this.trophyUpdateValidator.validate(trophy, bindingResult);

		  if (!bindingResult.hasErrors()) 
		  {
    		 if (venue != null && venue.isEmpty())
    		 {
    			 trophy.setVenue(venue);
    		 }
    		 
    		 if (imageTrophyFile != null) 
    		 {
    			 byte[] imageBytes = imageTrophyFile.getBytes();
    			 trophy.setImage(imageBytes);
    		 }
    		 
    		 if (imageWin != null) 
    		 {
    			 byte[] imageBytes = imageWin.getBytes();
    			 trophy.setImageWin(imageBytes);
    		 }
    		 
    		 this.trophyRepository.save(trophy);
    		 model.addAttribute("trophy",trophy);
    		 return "public/trophy";
    	}
    	else 
    	{
    		return "admin/formUpdateTrophy";
    	}
	}
	

	
	@Transactional
	@GetMapping("/admin/deleteTrophy/{id}")
	public String deleteTrophy(@PathVariable("id") Long id, Model model)
	{
		
		Trophy trophy = this.trophyRepository.findById(id).get();
		
		Team team= trophy.getWinningTeam();
		
		List<Trophy> trophies= this.trophyRepository.findByWinningTeam(team);
        
        
        for(Trophy trophyTeam: trophies) 
        {
        	if(team!=null)
        	{
        		team.getTrophy().remove(trophyTeam);
        		this.teamRepository.save(team);
        	}
        }
        
        this.trophyRepository.delete(trophy);
        
		model.addAttribute("trophies", this.trophyRepository.findAll());
		
		return "admin/managetrophies";
	}
	
	
	@GetMapping("/image/trophy/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) 
	{
	    Trophy trophy = trophyRepository.findById(id).orElse(null);
	    if (trophy != null && trophy.getImage() != null) 
	    {	        
	    	return trophy.getImage();
	    }
	    return new byte[0];
	}
	
	@GetMapping("/imageWin/trophy/{id}")
	@ResponseBody
	public byte[] getImageWin(@PathVariable("id") Long id) 
	{
	    Trophy trophy = trophyRepository.findById(id).orElse(null);
	    
	    if (trophy != null && trophy.getImageWin() != null) 
	    {
	        return trophy.getImageWin();
	    }
	    return new byte[0];
	}
}