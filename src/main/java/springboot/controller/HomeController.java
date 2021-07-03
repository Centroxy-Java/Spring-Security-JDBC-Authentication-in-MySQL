package springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
	@GetMapping("/user")
	public String Home() {
		return "Welcome";
	}
	
	@GetMapping("/")
	public String User() {
		return "Welcome User";
	}
	
	@GetMapping("/admin")
	public String admin() {
		return "Welcome admin";
	}
	
}
