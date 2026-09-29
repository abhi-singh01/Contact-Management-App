package com.nextgen.contactmanager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class
HomeController {

	@GetMapping("/")
	public String Home(){
		return "home";
	}
	@GetMapping("/register")
	public String register(){
		return "signup";
	}
	@GetMapping("/login")
	public String login(){
		return "signin";
	}
	@GetMapping("/login/contact")
	public String payment(){
		return "contact";
	}

	@GetMapping("/profile")
	public String profile(){
		return "profile";
	}

	@GetMapping("/settings")
	public String settings(){
		return "settings";
	}

}
