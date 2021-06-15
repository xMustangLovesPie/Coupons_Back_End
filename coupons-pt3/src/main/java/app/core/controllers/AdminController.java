package app.core.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.entities.Customer;
import app.core.exceptions.ServiceException;
import app.core.jwt.JwtUtil;
import app.core.payloads.CompanyPayload;
import app.core.payloads.CustomerPayload;
import app.core.services.AdminService;
import app.core.services.CompanyService;
import app.core.services.ImageUploadService;
import io.jsonwebtoken.ExpiredJwtException;

@CrossOrigin
@RestController
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired
	private JwtUtil tokenUtil;

	private boolean test(String token) {
		try {
			if (!tokenUtil.extractUsername(token).equalsIgnoreCase("admin@admin.com")) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong user type");
			}
			return true;
		} catch (ExpiredJwtException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
		}

	}

	@PostMapping("/api/admin/company/")
	public Company addCompany(@RequestHeader String token, @ModelAttribute CompanyPayload company) {
		test(token);
		try {
			return adminService.addCompany(company);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@PutMapping("/api/admin/company/")
	public Company updateCompany(@RequestHeader String token, @RequestBody Company company) {
		test(token);
		try {
			return adminService.updateCompany(company);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@DeleteMapping("/api/admin/company/{id}")
	public void deleteCompany(@RequestHeader String token, @PathVariable int id) {
		test(token);
		Company company = companyService.getDetails(id);
		List<Coupon> coupons = company.getCoupons();
		for (Coupon coupon : coupons) {
			imageUploadService.DeleteFile(Integer.toString(coupon.getId()));
		}
		try {
			adminService.deleteCompany(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/company/")
	public List<Company> getAllCompanies(@RequestHeader String token) {
		test(token);
		return adminService.getAllCompanies();
	}
//	@GetMapping("/api/admin/company/get-all/")
//	public List<Company> getAllCompanies(@RequestHeader String token) {
//		test(token);
//		return adminService.getAllCompanies();
//	}

	@GetMapping("/api/admin/company/get-one-lazy/{id}")
	public Company getOneCompanyWithoutCoupons(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			return adminService.getOneCompanyWithoutCoupons(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/company/get-one-eager/{id}")
	public Company getOneCompanyWithCoupons(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			return adminService.getOneCompanyWithCoupons(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PostMapping("/api/admin/customer/")
	public Customer addCustomer(@RequestHeader String token, @ModelAttribute CustomerPayload customer) {
		test(token);
		try {
			return adminService.addCustomer(customer);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@PutMapping("/api/admin/customer/")
	public Customer updateCustomer(@RequestHeader String token, @RequestBody Customer customer) {
		test(token);
		try {
			return adminService.updateCustomer(customer);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@DeleteMapping("/api/admin/customer/{id}")
	public void deleteCustomer(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			adminService.deleteCustomer(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/customer/")
	public List<Customer> getAllCustomers(@RequestHeader String token) {
		test(token);
		return adminService.getAllCustomers();
	}

	@GetMapping("/api/admin/customer/get-one-lazy/{id}")
	public Customer getOneCustomerWithoutCoupons(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			return adminService.getOneCustomerWithoutCoupons(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/customer/get-one-eager/{id}")
	public Customer getOneCustomerWithCoupons(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			return adminService.getOneCustomerWithCoupons(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/coupon/get-one/{id}")
	public Coupon getOneCoupon(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			return adminService.getOneCoupon(id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/admin/coupon/")
	public List<Coupon> getAllCoupons(@RequestHeader String token) {
		test(token);
		return adminService.getAllCoupons();
	}
}
