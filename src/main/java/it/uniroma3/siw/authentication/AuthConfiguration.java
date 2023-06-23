package it.uniroma3.siw.authentication;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.UserRepository;

import javax.sql.DataSource;
import it.uniroma3.siw.model.Credentials;
import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;
@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter 
{
	
	@Autowired DataSource datasource;
	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{
		http.anonymous().authorities("ROLE_ANONYMOUS");
		
		http.authorizeRequests() 
		.antMatchers(HttpMethod.GET, "/", "/signup", "/login", "/default", "/css/**","/images/**","/image/**","/imageWin/**").permitAll()
		
		.antMatchers(HttpMethod.POST, "/login", "/signup", "/register").permitAll()
		
		.antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
		.antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
		
		
		.antMatchers(HttpMethod.GET, "/default/**").hasAnyAuthority(DEFAULT_ROLE)
		.antMatchers(HttpMethod.POST,"/default/**").hasAnyAuthority(DEFAULT_ROLE) 
		
		.antMatchers(HttpMethod.GET, "/public/**").hasAnyAuthority("ROLE_ANONYMOUS",DEFAULT_ROLE)
		.antMatchers(HttpMethod.POST,"/public/**").hasAnyAuthority("ROLE_ANONYMOUS",DEFAULT_ROLE) 
		
		
		.anyRequest().authenticated()
		.and().formLogin() 
		
		.loginPage("/login")
		
		.defaultSuccessUrl("/default",true)
		.and()
		.logout()
		.logoutUrl("/logout")
		.logoutSuccessUrl("/")
		.invalidateHttpSession(true)
		.deleteCookies("JSESSIONID")
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.clearAuthentication(true).permitAll();
	}
	
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception 
	{
		auth.jdbcAuthentication()
		
		.dataSource(this.datasource) 
		
		.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?")
		
		.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
		
	    
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        auth.inMemoryAuthentication()
            .passwordEncoder(passwordEncoder)
            .withUser("admin")
            .password(passwordEncoder.encode("admin"))
            .roles("ADMIN");
        
        auth.userDetailsService(userDetailsService());
	}
	
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	/*
	@Bean
	public CommandLineRunner initDB(UserRepository userRepository, CredentialsRepository credentialsRepository) {
	    return (args) -> {
	        // creazione dell'utente
	    	
	    	
	        Credentials adminCredentials = new Credentials();
	        adminCredentials.setUsername("admin");
	        adminCredentials.setPassword(new BCryptPasswordEncoder().encode("admin"));

	        // creazione delle credenziali associate all'utente
	        adminCredentials.setRole("ADMIN");
	        credentialsRepository.save(adminCredentials);
	    };
	    
	}
	*/
	
}

	

