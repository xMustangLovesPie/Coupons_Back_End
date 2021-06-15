package app.core.controllers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.LoginException;
import app.core.jwt.JwtUtil;
import app.core.jwt.JwtUtil.UserDetails;
import app.core.login.Credentials;
import app.core.login.LoginManager;
import app.core.login.ServiceType;
import app.core.services.CompanyService;
import app.core.services.CustomerService;

@CrossOrigin
@RestController
public class LoginController {

	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private LoginManager loginManager;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CustomerService customerService;

	@PostMapping("/login/{serviceType}")
	public String loginAdmin(@RequestBody Credentials credentials, @PathVariable ServiceType serviceType) {
		try {
			String email = credentials.getEmail();
			String password = credentials.getPassword();
			int id = 0;
			String firstName = "";
			String lastName = "";
			switch (serviceType) {
			case ADMINISTRATOR:
				firstName = "Admin";
				break;
			case COMPANY:
				Company company = companyService.getDetails(email);
				if (company == null) {
					break;
				}
				firstName = company.getName();
				id = company.getId();
				break;
			default:
				Customer customer = customerService.getDetails(email);
				if (customer == null) {
					break;
				}
				firstName = customer.getFirstName();
				lastName = customer.getLastName();
				id = customer.getId();
				break;
			}
			loginManager.login(email, password, serviceType);
			String jwt = jwtUtil.generateToken(new UserDetails(email, firstName, lastName, serviceType));
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("email", email);
			jsonObject.put("firstName", firstName);
			jsonObject.put("lastName", lastName);
			jsonObject.put("userType", serviceType);
			jsonObject.put("token", jwt);
			jsonObject.put("id", id);
			return jsonObject.toString();
		} catch (LoginException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
	}
}