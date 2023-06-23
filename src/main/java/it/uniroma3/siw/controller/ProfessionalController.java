package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import it.uniroma3.siw.controller.validator.ProfessionalUpdateValidator;
import it.uniroma3.siw.controller.validator.ProfessionalValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Rating;
import it.uniroma3.siw.model.Trophy;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ProfessionalRepository;
import it.uniroma3.siw.repository.RatingRepository;
import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TrophyRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class ProfessionalController 
{
	@Autowired ProfessionalRepository professionalRepository;
	@Autowired ProfessionalValidator professionalValidator;
	@Autowired ProfessionalUpdateValidator professionalUpdateValidator;
	@Autowired UserRepository userRepository;
	@Autowired RatingRepository ratingRepository;
	@Autowired TeamRepository teamRepository;
	@Autowired TrophyRepository trophyRepository;
	@Autowired
	private CredentialsService credentialsService;
	
	 
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	
	@GetMapping("/admin/formNewProfessional")
	public String formNewProfessional(Model model) 
	{
		model.addAttribute("professional", new Professional());
		return "admin/formNewProfessional";	
	}
	
	@GetMapping("/public/formSearchProfessionals")
	public String formSearchProfessionals()
	{
		return "public/formSearchProfessionals";
	}
	
	
	@PostMapping("/public/searchProfessionals")
	public String formSearchProfessionals(Model model, @RequestParam ("name") String name, @RequestParam ("nationality") String nationality, 
			@RequestParam ("position") String position) 
	{
		if(name.isEmpty() && nationality == "" && position == "" )
		{
			model.addAttribute("professionals", null);
		}
		
		if(!name.isEmpty() && nationality == "" && position == "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByName(name));
		}
		
		if(name.isEmpty() && nationality != "" && position == "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNationality(nationality));
		}
		
		if(name.isEmpty() && nationality == "" && position != "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByPosition(position));
		}
		
		if(name.isEmpty() && nationality != "" && position != "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNationalityAndPosition(nationality,position));
		}
		
		if(!name.isEmpty() && nationality != "" && position == "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNameAndNationality(name,nationality));
		}
		
		if(!name.isEmpty() && nationality == "" && position != "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNameAndPosition(name,position));
		}
		
		if(name.isEmpty() && nationality != "" && position != "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNationalityAndPosition(nationality,position));
		}
		
		if(!name.isEmpty() && nationality != "" && position != "")
		{
			model.addAttribute("professionals", this.professionalRepository.findByNameAndNationalityAndPosition(name,nationality,position));
		}
		
		return "public/foundProfessionals";
	}
	
	
	@GetMapping("/default/myProfessionals")
	public String myList(Model model) 
	{
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	List<Professional>professionals= (List<Professional>) user.getMyProfessionals();

		model.addAttribute("professionals",professionals);
		return "default/myProfessionals";
	}
	
	
	@GetMapping("/default/addMyProfessional/{id}")
	public String addMyList(@PathVariable("id") Long id, Model model) 
	{
		boolean trovato=false;
		Professional professional=this.professionalRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	user.getMyProfessionals().add(professional);
    	this.userRepository.save(user);
    	
		for (Rating rating : user.getMyRatings()) 
		{
			if (rating.getProfessional().getId().equals(professional.getId())) 
			{
				trovato = true;
		        break; //esci dal ciclo for
		     }
		}
    	
    	 for (Professional myProfessional : user.getMyProfessionals()) 
    	 {
             if (myProfessional.getId().equals(professional.getId())) 
             {
                 model.addAttribute("professional", professional);
                 model.addAttribute("flag", 0);
                 model.addAttribute("rating",trovato);
                 return "public/professional";
             }
         }
		
		model.addAttribute("professional", professional);
		model.addAttribute("rating",trovato);
		model.addAttribute("flag", 1);
		return "public/team";
	}
	
	@GetMapping("/default/removeMyProfessional/{id}")
	public String removeMyTeam(@PathVariable("id") Long id, Model model) 
	{
		boolean trovato=false;
		Professional professional=this.professionalRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	user.getMyProfessionals().remove(professional);
    	this.userRepository.save(user);
    	
    	for (Rating rating : user.getMyRatings()) 
		{
			if (rating.getProfessional().getId().equals(professional.getId())) 
			{
				trovato = true;
		        break; // Esci dal ciclo for
		     }
		}
    	
    	 for (Professional myProfessional : user.getMyProfessionals()) 
    	 {
             if (myProfessional.getId().equals(professional.getId())) 
             {
                 model.addAttribute("professional", professional);
                 model.addAttribute("flag", 0);
                 model.addAttribute("rating",trovato);
                 return "public/professional";
             }
         }
		
		model.addAttribute("professional", professional);
		model.addAttribute("flag", 1);
		model.addAttribute("rating",trovato);
		return "public/professional";
	}
	
	@GetMapping("/public/professionals/{id}")
	public String getProfessional(@PathVariable("id") Long id, Model model,Authentication authentication) 
	{
		
		Professional professional=this.professionalRepository.findById(id).get();
		boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
		boolean trovato = false;
		if (isAuthenticated) 
		{
		    String username = authentication.getName();
		    Credentials credentials = credentialsService.getCredentials(username);
		    User user = credentials.getUser();


		    for (Rating rating : user.getMyRatings()) {
		        if (rating.getProfessional().getId().equals(professional.getId())) {
		            trovato = true;
		            break; // Esci dal ciclo for
		        }
		    }

		    for (Professional myProfessional : user.getMyProfessionals()) {
		        if (myProfessional.getId().equals(professional.getId())) {
		            model.addAttribute("rating", trovato);
		            model.addAttribute("professional", professional);
		            model.addAttribute("trophies", professional.getTrophy());
		            model.addAttribute("flag", 0);
		            return "public/professional";
		        }
		    }
		}

		
		 	model.addAttribute("professional", professional);
		 	model.addAttribute("rating", trovato);
		 	model.addAttribute("trophies",professional.getTrophy());
		    model.addAttribute("flag", 1);
		    return "public/professional";
	}
	

	@Transactional
	@GetMapping("/admin/addTrophiesToProfessional/{id}")
	public String addTrophyToProfessional(@PathVariable("id") Long id, Model model) 
	{	
		 	Professional professional = this.professionalRepository.findById(id).get();
		    
		    List<Trophy> myTrophies = professional.getTrophy();
		    List<Trophy> trophies = (List<Trophy>) this.trophyRepository.findAll(); 

		    if (professional.getPosition().equals("Allenatore")) 
		    {
		        Iterator<Trophy> trophyIterator = trophies.iterator();
		        while (trophyIterator.hasNext()) 
		        {
		            Trophy trophy = trophyIterator.next();
		            for (Professional professionalWin : trophy.getWinningProfessionals()) 
		            {
		                if (professionalWin.getPosition().equals("Allenatore")) 
		                {
		                    trophyIterator.remove();
		                    break;
		                }
		            }
		        } 
		        /*Il controllo inizia con la verifica che il professionista in questione sia effettivamente un Allenatore.
		         *Verificata la posizione di "Allenatore" del professionista, si rimuove dalla lista dei trofei presenti sul database
		         *quelli già conseguiti da un altro allenatore, in quanto un solo allenatore può conseguire uno specifico trofeo.
		         */
		        model.addAttribute("trophies", trophies);
		        model.addAttribute("myTrophies", myTrophies);
		        model.addAttribute("size",myTrophies.size());
		        model.addAttribute("professional", professional);
		        return "admin/trophiesToAddProfessional";
		    }

		    Iterator<Trophy> trophyIterator = trophies.iterator();
		    while (trophyIterator.hasNext()) 
		    {
		        Trophy trophy = trophyIterator.next();
		        for (Trophy myTrophy : myTrophies) 
		        {
		            if (trophy.getId().equals(myTrophy.getId())) 
		            {
		                trophyIterator.remove();
		                break;
		            }
		        }
		    }
		    
		    /*Altrimenti, se il professionista è un Giocatore allora si rimuovono dalla lista dei trofei quelli già conseguiti da altri 
		     * giocatori.
		     */
		    
		    model.addAttribute("trophies", trophies);
		    model.addAttribute("myTrophies", myTrophies);
		    model.addAttribute("size",myTrophies.size());
		    model.addAttribute("professional", professional);
		    return "admin/trophiesToAddProfessional";
	}
	
	
	
	@Transactional
	@GetMapping("/admin/updateTrophiesOfProfessional/{id}")
	public String updateTrophyOfProfessional(@PathVariable("id") Long id, Model model) 
	{
	    Professional professional = this.professionalRepository.findById(id).get();
	    
	    List<Trophy> myTrophies = professional.getTrophy();
	    List<Trophy> trophies = (List<Trophy>) this.trophyRepository.findAll(); 

	    if (professional.getPosition().equals("Allenatore")) 
	    {
	        Iterator<Trophy> trophyIterator = trophies.iterator();
	        while (trophyIterator.hasNext()) 
	        {
	            Trophy trophy = trophyIterator.next();
	            for (Professional professionalWin : trophy.getWinningProfessionals()) 
	            {
	                if (professionalWin.getPosition().equals("Allenatore")) 
	                {
	                    trophyIterator.remove();
	                    break;
	                }
	            }
	        }

	        model.addAttribute("trophies", trophies);
	        model.addAttribute("myTrophies", myTrophies);
	        model.addAttribute("professional", professional);
	        return "admin/trophiesToAddProfessional";
	    }

	    Iterator<Trophy> trophyIterator = trophies.iterator();
	    while (trophyIterator.hasNext()) 
	    {
	        Trophy trophy = trophyIterator.next();
	        for (Trophy myTrophy : myTrophies) 
	        {
	            if (trophy.getId().equals(myTrophy.getId())) 
	            {
	                trophyIterator.remove();
	                break;
	            }
	        }
	    }

	    model.addAttribute("trophies", trophies);
	    model.addAttribute("myTrophies", myTrophies);
	    model.addAttribute("professional", professional);
	    return "admin/trophiesToAddProfessional";
	}

	
	
	@Transactional
	@GetMapping("/admin/removeTrophyFromProfessional/{trophyid}/{professionalid}")
	public String removeTrophyFromProfessional(
	    @PathVariable("trophyid") Long trophyId,
	    @PathVariable("professionalid") Long professionalId,
	    Model model) 
		{
		
	    Professional professional = this.professionalRepository.findById(professionalId).get();
	    Trophy trophy = this.trophyRepository.findById(trophyId).get();
	    
	    List<Trophy> myTrophies = professional.getTrophy();
	    List<Trophy> trophies = (List<Trophy>) this.trophyRepository.findAll();

	    professional.getTrophy().remove(trophy);
	    this.professionalRepository.save(professional);

	    trophy.getWinningProfessionals().remove(professional);
	    this.trophyRepository.save(trophy);

	    
	    if (professional.getPosition().equals("Allenatore")) 
	    {
	        Iterator<Trophy> trophyIterator = trophies.iterator();
	        while (trophyIterator.hasNext()) 
	        {
	            Trophy currentTrophy = trophyIterator.next();
	            for (Professional professionalWin : currentTrophy.getWinningProfessionals()) 
	            {
	                if (professionalWin.getPosition().equals("Allenatore")) 
	                {
	                    trophyIterator.remove();
	                    break;
	                }
	            }
	        }

	        model.addAttribute("trophies", trophies);
	        model.addAttribute("myTrophies", myTrophies);
	        model.addAttribute("size", myTrophies.size());
	        model.addAttribute("professional", professional);
	        return "admin/trophiesToAddProfessional";
	    }

	    Iterator<Trophy> trophyIterator = trophies.iterator();
	    while (trophyIterator.hasNext()) 
	    {
	        Trophy currentTrophy = trophyIterator.next();
	        for (Trophy myTrophy : myTrophies) 
	        {
	            if (currentTrophy.getId().equals(myTrophy.getId())) 
	            {
	                trophyIterator.remove();
	                break;
	            }
	        }
	    }

	    model.addAttribute("trophies", trophies);
	    model.addAttribute("myTrophies", myTrophies);
	    model.addAttribute("size", myTrophies.size());
	    model.addAttribute("professional", professional);
	    return "admin/trophiesToAddProfessional";
	}

	
	
	@Transactional
	@GetMapping("/admin/setTrophiesToProfessional/{trophyid}/{professionalid}")
	public String setTrophyToTeam(@PathVariable("trophyid") Long trophyId, @PathVariable("professionalid") Long professionalId, Model model) 
	{
		
		Professional professional = this.professionalRepository.findById(professionalId).get();
		Trophy trophy = this.trophyRepository.findById(trophyId).get();

	    List<Trophy> trophies = (List<Trophy>) this.trophyRepository.findAll();

	    
	    professional.getTrophy().add(trophy);
	    this.professionalRepository.save(professional);
	    
	    trophy.getWinningProfessionals().add(professional);
	    this.trophyRepository.save(trophy);

	    if (professional.getPosition().equals("Allenatore")) 
	    {
	        Iterator<Trophy> trophyIterator = trophies.iterator();
	        while (trophyIterator.hasNext()) 
	        {
	            Trophy currentTrophy = trophyIterator.next();
	            for (Professional professionalWin : currentTrophy.getWinningProfessionals()) 
	            {
	                if (professionalWin.getPosition().equals("Allenatore")) 
	                {
	                    trophyIterator.remove();
	                    break;
	                }
	            }
	        }

	        model.addAttribute("trophies", trophies);
	        model.addAttribute("myTrophies", professional.getTrophy());
	        model.addAttribute("size",professional.getTrophy().size());
	        model.addAttribute("professional", professional);
	        return "admin/trophiesToAddProfessional";
	    }

	    Iterator<Trophy> trophyIterator = trophies.iterator();
	    while (trophyIterator.hasNext()) 
	    {
	        Trophy currentTrophy = trophyIterator.next();
	        for (Trophy myTrophy : professional.getTrophy()) 
	        {
	            if (currentTrophy.getId().equals(myTrophy.getId())) 
	            {
	                trophyIterator.remove();
	                break;
	            }
	        }
	    }

	    model.addAttribute("trophies", trophies);
	    model.addAttribute("myTrophies", professional.getTrophy());
	    model.addAttribute("size",professional.getTrophy().size());
	    model.addAttribute("professional", professional);
	    return "admin/trophiesToAddProfessional";
	}

	

	@GetMapping("/admin/formUpdateProfessional/{id}")
	public String getUpdateProfessional(@PathVariable("id") Long id, Model model) 
	{
		
		Professional professional=this.professionalRepository.findById(id).get();
		
		model.addAttribute("size",professional.getTrophy().size());
		model.addAttribute("professional", professional);
		
		return "admin/formUpdateProfessional";
	}
	
	@PostMapping("/admin/professional/edit/{id}")
	public String editProfessional(
	    @Valid @ModelAttribute("professional") Professional professional,
	    BindingResult bindingResult,
	    @PathVariable("id") Long id,
	    Model model,
	    @RequestParam("name") String name,
	    @RequestParam("surname") String surname,
	    @RequestParam("nationality") String nationality,
	    @RequestParam(name = "isCaptain", required = false) Boolean isCaptain,
	    @RequestParam("height") String height,
	    @RequestParam("dateOfBirth") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate dateOfBirth,
	    @RequestParam("goals") Integer goals,
	    @RequestParam("footedness") String footedness,
	    @RequestParam("position") String position,
	    @RequestParam("image") MultipartFile imageProfessionalFile
	) throws IOException 
	
	{
		
		float valutazione=0;
	    this.professionalUpdateValidator.validate(professional, bindingResult);
	    List<Rating>RatingsProfessional= this.ratingRepository.findByProfessional(professional);

	    if (!bindingResult.hasErrors()) 
	    {
	    	
	    	if (dateOfBirth != null) 
	    	{
	    		professional.setDateOfBirth(dateOfBirth);
	    	}
	    	
	    	if (nationality != null && !nationality.isEmpty()) 
	    	{
	    		professional.setNationality(nationality);
	    	}

	    	if (goals != null) 
	    	{
	    		professional.setGoals(goals);
	    	}

	    	if (footedness != null && !footedness.isEmpty()) 
	    	{
	    		professional.setFootedness(footedness);
	    	}

	    	if (position != null && !position.isEmpty() && position.equals("Allenatore")) 
	    	{
	    		professional.setPosition(position);
	    	}

	    	if (isCaptain != null && isCaptain) 
	    	{
	    		professional.setIsCaptain(isCaptain);
	    	}

	    	if (imageProfessionalFile != null) 
	    	{
	    		byte[] imageBytes = imageProfessionalFile.getBytes();
	    		professional.setImage(imageBytes);
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
	    	}
	    	
	    	float val= (float)(valutazione/RatingsProfessional.size());
	    	
	    	if(RatingsProfessional.size()==0)
	    	{
	    		if(professional.getPosition().equals("Allenatore"))
	    			professional.setValutationasCoach(0);
	    		else
	    			professional.setValutationasFootballer(0);
	    	}
	    	
	    	else
    		if(professional.getPosition().equals("Allenatore"))
    		{
    			professional.setValutationasCoach(val);
    		}
    		else
    		{
    		professional.setValutationasFootballer(val);
    		}
	    	
	    	this.professionalRepository.save(professional);
	    	model.addAttribute("professional", professional);
	    	return "public/professional";
	    }
	    
	    else
	    {	        
	    	model.addAttribute("professional", professional);
	        return "admin/formUpdateProfessional";
	    }
	}

	
	
	@Transactional
	@GetMapping("/admin/deleteProfessional/{id}")
	public String deleteProfessional(@PathVariable("id") Long id, Model model) 
	{
		
		Professional professional = this.professionalRepository.findById(id).get();
		List<User> users= this.userRepository.findByMyProfessionals(professional);
		List<Trophy>Trophies= this.trophyRepository.findByWinningProfessionals(professional);
		
		for(Trophy trophy: Trophies)
		{
			trophy.getWinningProfessionals().remove(professional);
			this.trophyRepository.save(trophy);
		}
		
		for(User user: users)
		{
			user.getMyProfessionals().remove(professional);
			this.userRepository.save(user);
		}
		
		if(professional.getTeamAsCoach()!=null)
		{
			if(professional.getTeamAsCoach().getId()!=null)
			{
				professional.getTeamAsCoach().setProfessionalasCoach(null);
				this.teamRepository.save(professional.getTeamAsCoach());
				professional.setTeamAsCoach(null);
				this.professionalRepository.save(professional);
			}
		}
		
		if(professional.getTeamAsFootballer()!=null)
		{
			if(professional.getTeamAsFootballer().getId()!=null)
			{
				professional.getTeamAsFootballer().getProfessionalasFootballer().remove(professional);
				this.teamRepository.save(professional.getTeamAsFootballer());
				professional.setTeamAsFootballer(null);
				this.professionalRepository.save(professional);
			}
		}
		
		
        this.professionalRepository.delete(professional);
        
		model.addAttribute("professionals", this.professionalRepository.findAll());
		
		return "admin/manageProfessionals";
		
	}
	
	
	@GetMapping("/admin/manageProfessionals")
	public String ManageProfessionals(Model model) 
	{

		model.addAttribute("professionals", this.professionalRepository.findAll());
		
		return "admin/manageProfessionals";
		
	}
	

	 @GetMapping("/public/professionals")
	 public String showProfessionals(Model model) 
	 {
		 model.addAttribute("professionals",this.professionalRepository.findAll());
		 return "public/professionals";
	 }
	
	
	@PostMapping("/admin/professionals") 
	public String newProfessional(@Valid @ModelAttribute("professional") Professional professional, BindingResult bindingResult, 
			Model model,@RequestParam("image") MultipartFile imageProfessionalFile)throws IOException 
	{
		
		this.professionalValidator.validate(professional, bindingResult);
		
		if (!bindingResult.hasErrors()) 
		{
			byte[] imageBytes = imageProfessionalFile.getBytes();
			
    		professional.setImage(imageBytes);
    		
    		this.professionalRepository.save(professional);
    		
			model.addAttribute("professional", professional);
			
			return "public/professional"; 
		}
		else 
		{
			return "admin/formNewProfessional";
		}
	}
	
	@GetMapping("/image/professional/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) {
	    Professional professional = professionalRepository.findById(id).orElse(null);
	    
	    if (professional != null && professional.getImage() != null) {
	        // Restituisce l'immagine come array di byte
	        return professional.getImage();
	    }
	    return new byte[0];
	}
}