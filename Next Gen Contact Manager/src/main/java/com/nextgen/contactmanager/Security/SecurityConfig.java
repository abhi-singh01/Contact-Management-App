package com.nextgen.contactmanager.Security;

import com.nextgen.contactmanager.Service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Autowired
	private MyUserDetailsService service;
	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(service);
		provider.setPasswordEncoder(encoder());
		return provider;
	}
	@Bean
 SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception { 
	 return security.csrf(csrf->csrf.disable()).authorizeHttpRequests(auth ->
   auth.requestMatchers("/","/signup/user","/register/**").permitAll() .requestMatchers("/**")
  .hasRole("USER").anyRequest().authenticated()).formLogin(form->form.defaultSuccessUrl("/login/contact").loginPage("/login")
		  .permitAll()).build();
  }
}
