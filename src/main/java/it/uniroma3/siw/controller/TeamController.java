package it.uniroma3.siw.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import it.uniroma3.siw.controller.validator.TeamUpdateValidator;
import it.uniroma3.siw.controller.validator.TeamValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Professional;
import it.uniroma3.siw.model.Team;
import it.uniroma3.siw.model.Trophy;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ProfessionalRepository;
import it.uniroma3.siw.repository.UserRepository;

import it.uniroma3.siw.repository.TeamRepository;
import it.uniroma3.siw.repository.TrophyRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class TeamController 
{
	@Autowired TeamRepository teamRepository;
	@Autowired UserRepository userRepository;
	@Autowired ProfessionalRepository professionalRepository;
	@Autowired TeamValidator teamValidator;
	@Autowired TeamUpdateValidator teamUpdateValidator;
	@Autowired TrophyRepository trophyRepository;
	@Autowired
	private CredentialsService credentialsService;
	
	
	 
	@InitBinder
	public void initBinder(WebDataBinder binder) 
	{
	    binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	
	@GetMapping("/default/myTeams")
	public String myTeams(Model model) 
	{
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	List<Team>teams= (List<Team>) user.getMyTeams();
    	List<Team>teamsItaly= new ArrayList<>();
		List<Team>teamsSpain= new ArrayList<>();
		List<Team>teamsEn= new ArrayList<>();
		List<Team>teamsFrance= new ArrayList<>();
		List<Team>teamsPortugal= new ArrayList<>();
		List<Team>teamsGermany= new ArrayList<>();
		
		for(Team team : teams)
		{
			if(team.getNationality().equals("italy"))
				teamsItaly.add(team);	
			if(team.getNationality().equals("spain"))
				teamsSpain.add(team);
			if(team.getNationality().equals("england"))
				teamsEn.add(team);
			if(team.getNationality().equals("france"))
				teamsFrance.add(team);
			if(team.getNationality().equals("portugal"))
				teamsPortugal.add(team);
			if(team.getNationality().equals("germany"))
				teamsGermany.add(team);
		}
    	
		model.addAttribute("teamsItaly", teamsItaly );
		model.addAttribute("teamsSpain", teamsSpain);
		model.addAttribute("teamsEngland", teamsEn);
		model.addAttribute("teamsFrance", teamsFrance);
		model.addAttribute("teamsPortugal",teamsPortugal );
		model.addAttribute("teamsGermany", teamsGermany);
		return "default/myTeams";
	}
	
	
	@GetMapping("/default/addMyTeam/{id}")
	public String addMyTeam(@PathVariable("id") Long id, Model model) 
	{
		
		Team team=this.teamRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	user.getMyTeams().add(team);
    	this.userRepository.save(user);
    	
    	 for (Team myTeam : user.getMyTeams()) 
    	 {
             if (myTeam.getId().equals(team.getId())) 
             {
                 model.addAttribute("team", team);
                 model.addAttribute("trophies", team.getTrophy());
                 model.addAttribute("flag", 0);
                 return "public/team";
             }
         }
		
		model.addAttribute("team", team);
		model.addAttribute("flag", 1);
		model.addAttribute("trophies", team.getTrophy());
		return "public/team";
	}
	
	@GetMapping("/default/removeMyTeam/{id}")
	public String removeMyTeam(@PathVariable("id") Long id, Model model) 
	{
		
		Team team=this.teamRepository.findById(id).get();
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	User user= credentials.getUser();
    	
    	user.getMyTeams().remove(team);
    	this.userRepository.save(user);
    	
    	 for (Team myTeam : user.getMyTeams()) 
    	 {
             if (myTeam.getId().equals(team.getId())) 
             {
                 model.addAttribute("team", team);
                 model.addAttribute("trophies", team.getTrophy());
                 model.addAttribute("flag", 0);
                 return "public/team";
             }
         }
		
		model.addAttribute("team", team);
		model.addAttribute("flag", 1);
		model.addAttribute("trophies", team.getTrophy());
		return "public/team";
	}
	
	
	
	@GetMapping("/admin/formNewTeam")
	public String formNewTeam(Model model) 
	{
		model.addAttribute("team", new Team());
		return "admin/formNewTeam";
	}
	

	
	@PostMapping("/admin/teams")
	public String newTeam(@Valid @ModelAttribute("team") Team team,
	                       BindingResult bindingResult,
	                       Model model,
	                       @RequestParam("image") MultipartFile imageFile)throws IOException
	   {

	    	this.teamValidator.validate(team, bindingResult);

	    	if (!bindingResult.hasErrors()) 
	    	{
	    		byte[] imageBytes = imageFile.getBytes();
	        
	    		team.setImage(imageBytes);
	    		this.teamRepository.save(team);
	    		model.addAttribute("team", team);
	    		return "public/team";
	    	}
	    	else 
	    	{
	    		return "admin/formNewTeam";
	    	}
	        
	    }
	
	
	@GetMapping("/admin/manageTeams")
	public String manageTeams(Model model) 
	{
		model.addAttribute("teams", this.teamRepository.findAll());
		return "admin/manageTeams";
		
	}
	
	
	@GetMapping("/admin/formUpdateTeam/{id}")
	public String getUpdateTeam(@PathVariable("id") Long id, Model model) 
	{
		
		model.addAttribute("team", this.teamRepository.findById(id).get());
		
		Team team=this.teamRepository.findById(id).get();
		
		model.addAttribute("team",team);
		model.addAttribute("size",team.getTrophy().size());
        
		return "admin/formUpdateTeam";
	}
	
	
	@PostMapping("/admin/team/edit/{id}")
	public String editTeam(@Valid @ModelAttribute("team") Team team,BindingResult bindingResult,@PathVariable("id") Long id,Model model,
			@RequestParam("image") MultipartFile imageTeamFile,@RequestParam("numberOfPlayer") Integer numberOfPlayer,
			@RequestParam("stadium")String stadium, @RequestParam("foundedDate") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate foundedDate, 
			@RequestParam("hometown")String hometown,@RequestParam("nationality")String nationality, 
			@RequestParam("stadiumCapacity")Integer stadiumcapacity,@RequestParam("president")String president,
			@RequestParam(name = "isOnTopDivision", required = false)Boolean isOnTopDivision
			) throws IOException
	{
		
		  this.teamUpdateValidator.validate(team, bindingResult);

		  if (!bindingResult.hasErrors()) 
		  {
			 
    		 if (numberOfPlayer != null)
    		 {
    			 team.setNumberOfPlayer(numberOfPlayer);
    		 }
    		 
    		 if (stadium != null && !stadium.isEmpty()) 
    		 {
    			 team.setStadium(stadium);
    		 }
    		 
    		 if (nationality != null && !nationality.isEmpty()) 
    		 {
    			 team.setNationality(nationality);
    		 }
    		 
    		 if (hometown != null && !hometown.isEmpty()) 
    		 {
    			 team.setHometown(hometown);
    		 }
    		 
    		 if (president != null && !president.isEmpty()) 
    		 {
    			 team.setPresident(president);
    		 }
    		 
    		 if (foundedDate != null) 
    		 {
    			 team.setFoundedDate(foundedDate);
    		 }
    		 
    		 if (stadiumcapacity != null)
    		 {
    			 team.setStadiumCapacity(stadiumcapacity);
    		 }
		 
    		 if (isOnTopDivision != null && isOnTopDivision == true ) 
    		 {
    			 team.setIsOnTopDivision(isOnTopDivision);
    		 }
    		 else
    		 {
    			 team.setIsOnTopDivision(false);
    		 }
    		 
    		 if (imageTeamFile != null) 
    		 {
    			 byte[] imageBytes = imageTeamFile.getBytes();
    			 team.setImage(imageBytes);
    		 }
		 
    		 this.teamRepository.save(team);
    		 model.addAttribute("team",team);
    		 return "public/team";
    	}
    	else 
    	{
    		return "admin/formUpdateTeam";
    	}
	}
	

	@GetMapping("/admin/addCoachToTeam/{id}")
	public String addCoachToTeam(@PathVariable("id") Long id, Model model) {
		
		Professional coachTeam= this.teamRepository.findById(id).get().getProfessionalasCoach();
		
		List<Professional> professionalCoaches = new ArrayList<>();
		
		List<Professional> Professional_coach= (List<Professional>) this.professionalRepository.findAll(); 
		
		for(Professional coach: Professional_coach)
		{
			if(coach.getPosition().equals("Allenatore"))
				professionalCoaches.add(coach);
		}
		
		/*rimuovo dalla lista degli allenatori disponibili per essere aggiunti quello già selezionato come allenatore di quel Team.*/
		professionalCoaches.remove(coachTeam);
		
		model.addAttribute("team",this.teamRepository.findById(id).get());
		model.addAttribute("coaches",professionalCoaches);
		
		return "admin/coachesToAdd";
	}
	
	@GetMapping("/admin/setCoachToTeam/{coachid}/{teamid}")
	public String setCoachofTeam(@PathVariable("coachid") Long coachid, @PathVariable("teamid") Long teamid,Model model) 
	{ 
		
		Team team= this.teamRepository.findById(teamid).get();
		Professional coach= this.professionalRepository.findById(coachid).get();
		
		if(coach.getTeamAsCoach()!=null)
		{
			if(coach.getTeamAsCoach().getId()!=null)
			{
				coach.getTeamAsCoach().setProfessionalasCoach(null);
				this.teamRepository.save(coach.getTeamAsCoach());
				coach.setTeamAsCoach(null);
				this.professionalRepository.save(coach);
			}
		}
		
		team.setProfessionalasCoach(coach);
		coach.setTeamAsCoach(team);
		
		this.teamRepository.save(team);
		this.professionalRepository.save(coach);

		model.addAttribute("team",team);
		model.addAttribute("coach",team.getProfessionalasCoach());		
		
		return "admin/formUpdateTeam";
	}
	
	
	@Transactional
	@GetMapping("/admin/deleteTeam/{id}")
	public String deleteTeam(@PathVariable("id") Long id, Model model)
	{
		
		Team team = this.teamRepository.findById(id).get();
		
		List<Trophy> trophies= this.trophyRepository.findByWinningTeam(team);
        List<Professional> professionalsAsFootballer= this.professionalRepository.findByTeamAsFootballer(team);
        Professional professionalAsCoach= this.professionalRepository.findByTeamAsCoach(team);
        List<User> users= this.userRepository.findByMyTeams(team);
        
        
        for(Trophy trophy: trophies) 
        	trophy.setWinningTeam(null);
        
        for(Professional footballer: professionalsAsFootballer)
        {
        	footballer.setTeamAsFootballer(null);
        	this.professionalRepository.save(footballer);
        }
        
        for(User user: users )
        {
        	user.getMyTeams().remove(team);
        	this.userRepository.save(user);
        }
        
        if(professionalAsCoach!=null)
        {
        	professionalAsCoach.setTeamAsCoach(null);
        	this.professionalRepository.save(professionalAsCoach);
        }
        
        this.teamRepository.delete(team);
        
        
		model.addAttribute("teams", this.teamRepository.findAll());
		
		return "admin/manageTeams";
	}
	
	
	@GetMapping("/admin/updateFootballersOfTeam/{id}")
	public String updatePlayers(@PathVariable("id") Long id, Model model)
	{
		
		Team team= this.teamRepository.findById(id).get();
		
		List<Professional>players_asFootballer= new ArrayList<>();
		List<Professional>players= (List<Professional>) this.professionalRepository.findAll();
		
		boolean captain=false;
		
		for(Professional professional: team.getProfessionalasFootballer())
		{
			if(professional.getIsCaptain())
			{
				captain=true;
				break;
			}
		}
		
		for(Professional player: players)
		{
			if(!player.getPosition().equals("Allenatore"))
			{
				if(player.getTeamAsFootballer()!=null) 
				{
					/*se il giocatore appartiene già ad una squadra..*/
					if(player.getTeamAsFootballer().getId()!=team.getId())
					{
						/*se il giocatore in questione non appartiene a questo team.. si procede nel verificare se è capitano*/
						
						if(captain==true)
						{
							/*se la squadra ha già un capitano verifico se il giocatore è un capitano*/
							if(player.getIsCaptain()==false)
							{
								/*se non è capitano allora aggiungilo, altrimenti si passa ad un altro giocatore..*/
								players_asFootballer.add(player);
							}
						}
						/*altrimenti, se il capitano non è ancora presente all'interno del team, comunque mostra il professionista
						 *  all'interno della lista dei giocatori selezionabili
						 */
						else
						{
							players_asFootballer.add(player);
						}
					}
				}
				else
				{
					/*se il giocatore non appartiene ad una squadra verifichiamo semplicemente il fatto che sia capitano o meno..*/
					if (captain == true) 
					{
					    if (player.getIsCaptain() == false) 
					    { 
					        players_asFootballer.add(player);
					    }
					}
					else
					{
						players_asFootballer.add(player);
					}
				}
			}
		}
		
		model.addAttribute("footballers",team.getProfessionalasFootballer());
		model.addAttribute("team",team);
		model.addAttribute("players", players_asFootballer);
		
		return "admin/footballersToAdd";
	}
	

	@Transactional
	@GetMapping("/admin/addFootballerToTeam/{professionalid}/{teamid}")
	public String addFootballerToTeam(@PathVariable("professionalid") Long professionalid,@PathVariable("teamid")Long teamid, Model model) 
	{
		
	        Team team = this.teamRepository.findById(teamid).get();
	        Professional footballer = this.professionalRepository.findById(professionalid).get();
	        
	    	List<Professional>players_asFootballer= new ArrayList<>();
			List<Professional>players= (List<Professional>) this.professionalRepository.findAll();
			
			boolean captain=false;
			
			team.getProfessionalasFootballer().add(footballer);
	        this.teamRepository.save(team);
	        
	        
	        footballer.setTeamAsFootballer(team);
	        this.professionalRepository.save(footballer);
	        
	        
	        for(Professional professional: team.getProfessionalasFootballer())
			{
				if(professional.getIsCaptain())
				{
					captain=true;
					break;
				}
			}
			
			for(Professional player: players)
			{
				if(!player.getPosition().equals("Allenatore"))
				{
					if(player.getTeamAsFootballer()!=null) 
					{
						if(player.getTeamAsFootballer().getId()!=team.getId())
						{
							if(captain==true)
							{
								if(player.getIsCaptain()==false)
								{
									players_asFootballer.add(player);
								}
							}
							else
							{
								players_asFootballer.add(player);
							}
						}
					}
					else
					{
						if (captain == true) 
						{
						    if (player.getIsCaptain() == false) 
						    { 
						        players_asFootballer.add(player);
						    }
						}
						else
						{
							players_asFootballer.add(player);
						}
					}
				}
			}
			
	        
	        players_asFootballer.remove(footballer);

	        model.addAttribute("team",team);
	        model.addAttribute("footballers",team.getProfessionalasFootballer());
	        model.addAttribute("players",players_asFootballer);
	        
	        return "admin/footballersToAdd";
	    }

	
	@GetMapping("/admin/removeFootballerFromTeam/{professionalid}/{teamid}")
	public String removeFootballerFromTeam(@PathVariable("professionalid") Long professionalid,@PathVariable("teamid")Long teamid, Model model) 
	{
		
	        Team team = this.teamRepository.findById(teamid).get();
	        Professional footballer = this.professionalRepository.findById(professionalid).get();
	        
	        team.getProfessionalasFootballer().remove(footballer);
	        this.teamRepository.save(team);
	        
	        footballer.setTeamAsFootballer(null);
	        this.professionalRepository.save(footballer);

	        List<Professional>players_asFootballer= new ArrayList<>();
			List<Professional>players= (List<Professional>) this.professionalRepository.findAll();
			
			boolean captain=false;
			
			 for(Professional professional: team.getProfessionalasFootballer())
				{
					if(professional.getIsCaptain())
					{
						captain=true;
						break;
					}
				}
				
				for(Professional player: players)
				{
					if(!player.getPosition().equals("Allenatore"))
					{
						if(player.getTeamAsFootballer()!=null) 
						{
							if(player.getTeamAsFootballer().getId()!=team.getId())
							{
								if(captain==true)
								{
									if(player.getIsCaptain()==false)
									{
										players_asFootballer.add(player);
									}
								}
								else
								{
									players_asFootballer.add(player);
								}
							}
						}
						else
						{
							if (captain == true) 
							{
							    if (player.getIsCaptain() == false) 
							    { 
							        players_asFootballer.add(player);
							    }
							}
							else
							{
								players_asFootballer.add(player);
							}
						}
					}
				}
	        
	        model.addAttribute("team",team);
	        model.addAttribute("footballers",team.getProfessionalasFootballer());
	        model.addAttribute("players",players_asFootballer);
	        
	        return "admin/footballersToAdd";
	    }
	
	@GetMapping("/image/{id}")
	@ResponseBody
	public byte[] getImage(@PathVariable("id") Long id) {
	    
	    Team team = teamRepository.findById(id).orElse(null);
	    if (team != null && team.getImage() != null) {
	        // Restituisce l'immagine come array di byte
	        return team.getImage();
	    }
	    return new byte[0];
	}
	
	@Transactional
	@GetMapping("/public/teams/{id}")
	
	public String getTeam(@PathVariable("id") Long id, Model model, Authentication authentication) 
	{
		
	    Team team = this.teamRepository.findById(id).orElse(null);
	    boolean isAuthenticated = authentication != null && authentication.isAuthenticated();

	    if (isAuthenticated) 
	    {
	        String username = authentication.getName();
	        Credentials credentials = credentialsService.getCredentials(username);
	        User user = credentials.getUser();

	        for (Team myTeam : user.getMyTeams()) 
	        {
	            if (myTeam.getId().equals(team.getId())) 
	            {
	                model.addAttribute("team", team);
	                model.addAttribute("trophies", team.getTrophy());
	                model.addAttribute("flag", 0);
	                return "public/team";
	            }
	        }
	    }

	    model.addAttribute("team", team);
	    model.addAttribute("flag", 1);
	    model.addAttribute("trophies", team.getTrophy());
	    return "public/team";
	}



	@GetMapping("/public/teams")
	public String showTeams(Model model) 
	{
		List<Team>teams= (List<Team>) this.teamRepository.findAll();
		List<Team>teamsItaly= new ArrayList<>();
		List<Team>teamsSpain= new ArrayList<>();
		List<Team>teamsEn= new ArrayList<>();
		List<Team>teamsFrance= new ArrayList<>();
		List<Team>teamsPortugal= new ArrayList<>();
		List<Team>teamsGermany= new ArrayList<>();
		
		
		for(Team team : teams)
		{
			if(team.getNationality().equals("italy"))
				teamsItaly.add(team);	
			if(team.getNationality().equals("spain"))
				teamsSpain.add(team);
			if(team.getNationality().equals("england"))
				teamsEn.add(team);
			if(team.getNationality().equals("france"))
				teamsFrance.add(team);
			if(team.getNationality().equals("portugal"))
				teamsPortugal.add(team);
			if(team.getNationality().equals("germany"))
				teamsGermany.add(team);
		}
		
		model.addAttribute("teamsItaly", teamsItaly);
		model.addAttribute("teamsSpain", teamsSpain);
		model.addAttribute("teamsEngland", teamsEn);
		model.addAttribute("teamsFrance", teamsFrance);
		model.addAttribute("teamsPortugal",teamsPortugal);
		model.addAttribute("teamsGermany", teamsGermany);
		
		return "public/teams";
	}
	
	@GetMapping("/public/formSearchTeams")
	public String formSearchTeams() 
	{
		return "public/formSearchTeams";
	}

	@PostMapping("/public/searchTeams")
	public String searchTeams(Model model,@RequestParam("name")String name,@RequestParam ("nationality") String nationality, 
			@RequestParam ("stadium") String stadium)
	{
		if(nationality == "" && stadium.isEmpty() && name.isEmpty() )
		{
			model.addAttribute("teams", null);
		}
		
		if(nationality == "" && !stadium.isEmpty() && name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByStadium(stadium));
		}
		
		if(nationality == "" && stadium.isEmpty() && !name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByName(name));
		}
		
		if(nationality != "" && stadium.isEmpty() && name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByNationality(nationality));
		}
		
		if(nationality != "" && !stadium.isEmpty() && name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByStadiumAndNationality(stadium,nationality));
		}
		
		if(nationality != "" && stadium.isEmpty() && !name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByNameAndNationality(name,nationality));
		}
		
		if(nationality == "" && !stadium.isEmpty() && !name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByNameAndStadium(name,stadium));
		}
		
		if(nationality != "" && !stadium.isEmpty() && !name.isEmpty())
		{
			model.addAttribute("teams", this.teamRepository.findByNameAndStadiumAndNationality(name,stadium,nationality));
		}
		
		return "public/foundTeams";
	}

	
	
	@Transactional
	@GetMapping("/admin/addTrophiesToTeam/{id}")
	public String addTrophyToTeam(@PathVariable("id") Long id, Model model) 
	{
		
		Team team= this.teamRepository.findById(id).get();
		
		List<Trophy>Unclaimedtrophy= (List<Trophy>) this.trophyRepository.findAll(); 
		
		
		Iterator<Trophy> iterator = Unclaimedtrophy.iterator();
		while (iterator.hasNext()) 
		{
		    Trophy trophyUncl = iterator.next();
		    if (trophyUncl.getWinningTeam() != null) 
		    {
		        iterator.remove();
		    }
		}
		
		model.addAttribute("team",this.teamRepository.findById(id).get());
		model.addAttribute("trophies", team.getTrophy());
		model.addAttribute("Unclaimedtrophy",Unclaimedtrophy);
		
		return "admin/trophiesToAdd";
	}
	
	@Transactional
	@GetMapping("/admin/removeTrophyFromTeam/{trophyid}/{teamid}")
	public String removeTrophyFromTeam(@PathVariable("trophyid") Long trophyid,@PathVariable("teamid")Long teamid, Model model) 
	{
		
	        Team team = this.teamRepository.findById(teamid).get();
	        Trophy trophy = this.trophyRepository.findById(trophyid).get();
	        
	        
	        team.getTrophy().remove(trophy);
	        this.teamRepository.save(team);
	        
	        trophy.setWinningTeam(null);
	        this.trophyRepository.save(trophy);

	       
	        List<Trophy>Unclaimedtrophy= (List<Trophy>) this.trophyRepository.findAll();
			
			
	        Iterator<Trophy> iterator = Unclaimedtrophy.iterator();
			while (iterator.hasNext()) 
			{
			    Trophy trophyUncl = iterator.next();
			    if (trophyUncl.getWinningTeam() != null) 
			    {
			        iterator.remove();
			    }
			}			
	        
	        model.addAttribute("team",team);
	        model.addAttribute("trophies",team.getTrophy());
	        
	        model.addAttribute("Unclaimedtrophy",Unclaimedtrophy);
	        
	        return "admin/trophiesToAdd";
	    }
	
	
	
	@Transactional
	@GetMapping("/admin/updateTrophiesOfTeam/{id}")
	public String updateTrophyOfTeam(@PathVariable("id") Long id, Model model) 
	{
		
		Team team= this.teamRepository.findById(id).get();
		
		
		List<Trophy>Unclaimedtrophy= (List<Trophy>) this.trophyRepository.findAll();
		
		
		Iterator<Trophy> iterator = Unclaimedtrophy.iterator();
		while (iterator.hasNext()) 
		{
		    Trophy trophyUncl = iterator.next();
		    if (trophyUncl.getWinningTeam() != null) 
		    {
		        iterator.remove();
		    }
		}
		
		model.addAttribute("team",team);
		model.addAttribute("trophies",team.getTrophy()); //lista dei trofei della squadra
		model.addAttribute("Unclaimedtrophy",Unclaimedtrophy); //lista dei trofei che si possono aggiungere
		
		return "admin/trophiesToAdd";
	}
	
	
	@Transactional
	@GetMapping("/admin/setTrophiesToTeam/{trophyid}/{teamid}")
	public String setTrophyToTeam(@PathVariable("trophyid") Long trophyid, @PathVariable("teamid") Long teamid,Model model) 
	{ 
		
		Team team= this.teamRepository.findById(teamid).get();
		Trophy trophy= this.trophyRepository.findById(trophyid).get();
		List<Trophy>Unclaimedtrophy= (List<Trophy>) this.trophyRepository.findAll();
		
		
		if(trophy.getWinningTeam()==null)
		{
			team.getTrophy().add(trophy);
			this.teamRepository.save(team);
			trophy.setWinningTeam(team);
			this.trophyRepository.save(trophy);
		}
		
		Iterator<Trophy> iterator = Unclaimedtrophy.iterator();
		while (iterator.hasNext()) {
		    Trophy trophyUncl = iterator.next();
		    if (trophyUncl.getWinningTeam() != null) {
		        iterator.remove();
		    }
		}
		
		model.addAttribute("team",team);
		model.addAttribute("trophies",team.getTrophy());	
		model.addAttribute("Unclaimedtrophy",Unclaimedtrophy);
		
		return "admin/trophiesToAdd";
	}
}