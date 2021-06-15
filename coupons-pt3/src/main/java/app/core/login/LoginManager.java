package app.core.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import app.core.entities.Company;
import app.core.entities.Customer;
import app.core.exceptions.LoginException;
import app.core.services.AdminService;
import app.core.services.CompanyService;
import app.core.services.CustomerService;
import app.core.services.GeneralService;

@Component
public class LoginManager {

	@Autowired
	private ConfigurableApplicationContext context;

	public GeneralService login(String email, String password, ServiceType serviceType) throws LoginException {
		switch (serviceType) {
		
		
		case ADMINISTRATOR:
			AdminService adminService = context.getBean(AdminService.class);
			if (adminService.login(email, password)) {
				System.out.println("Logged in as admin");
				return adminService;
			}
			throw new LoginException("Cannot log in as Admin: Wrong email or password");

			
		case COMPANY:
			CompanyService companyService = context.getBean(CompanyService.class);
			Company company = companyService.login(email, password);
			if (company!=null) {
				System.out.println("Logged in as company");
				return companyService;
			}
			throw new LoginException("Cannot log in as Company: Wrong email or password");
			
			
		case CUSTOMER:
			CustomerService customerService = context.getBean(CustomerService.class);
			Customer customer = customerService.login(email, password);
			if (customer!=null) {
				System.out.println("Logged in as customer");
				return customerService;
			}
			throw new LoginException("Cannot log in as Customer: Wrong email or password");

			
		default:
			throw new LoginException("Login error. Make sure you are using the login method properly");
		}
	}
}