package app.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.entities.Customer;
import app.core.exceptions.ServiceException;
import app.core.payloads.CompanyPayload;
import app.core.payloads.CustomerPayload;

/**
 * Service class for the admin. 
 * All additional methods (such as {@link #getAllCompaniesIds}),
 * as well as an eager and non-eager version of each method,
 * are added for flexibility, for any future features, and are explained via JavaDoc.
 *
 * @author Noam Gonopolski
 */
@Service
@Transactional
public class AdminService extends GeneralService {

	public boolean login(String email, String password) {
		return email.equalsIgnoreCase("admin@admin.com") && password.equals("admin");
	}

	public Company addCompany(CompanyPayload company) throws ServiceException {
		// For phase 3 I will probably split this check to one for name and one for email
		// since in the front end it would probably look nicer this way. Still thinking about it :)
		// Keeping it like this for now since it does the job well.
		if (companyRepository.existsByNameOrEmail(company.getName(), company.getEmail())) {
			throw new ServiceException("Cannot add company. Name or Email already taken.");
		}
		Company companyInUse = new Company();
		companyInUse.setEmail(company.getEmail());
		companyInUse.setName(company.getName());
		companyInUse.setPassword(company.getPassword());
		return companyRepository.save(companyInUse);
	}

	public Company updateCompany(Company company) throws ServiceException {
		int id = company.getId();
		String email = company.getEmail();
		Optional<Company> opt = companyRepository.findById(id);
		if (opt.isPresent()) {
			if (!companyRepository.existsByEmailAndIdNot(email, id)) {
				Company companyFromDb = opt.get();
				companyFromDb.setEmail(email);
				companyFromDb.setPassword(company.getPassword());
				return companyFromDb;
			} else {
				throw new ServiceException("Cannot update company. Email: \"" + email + "\" already taken.");
			}
		}
		throw new ServiceException("Cannot update company. id: " + id + " doesn't exist.");
	}

	public void deleteCompany(int companyId) throws ServiceException {
		if (companyRepository.deleteByIdIs(companyId) == 0) {
			throw new ServiceException("Cannot delete company with id: " + companyId + ". ID Doesn't exist");
		}
	}

	public List<Company> getAllCompanies() {
		return companyRepository.findAll();
	}

	public List<Company> getAllCompaniesWithCoupons() {
		List<Company> companies = getAllCompanies();
		for (Company company : companies) {
			if (company != null) {
				company.setCoupons(couponRepository.findByCompanyId(company.getId()));
			}
		}
		return companies;
	}

	/**
	 * @return a List of the ID's of all the companies in the database.
	 * 
	 * @author Noam Gonopolski
	 */
	public List<Integer> getAllCompaniesIds() {
		List<Company> companies = getAllCompanies();
		List<Integer> companiesIds = new ArrayList<>();
		for (Company company : companies) {
			companiesIds.add(company.getId());
		}
		return companiesIds;
	}

	public Company getOneCompanyWithoutCoupons(int companyId) throws ServiceException {
		Optional<Company> opt = companyRepository.findById(companyId);
		if (opt.isPresent()) {
			Company company = opt.get();
			return company;
		}
		throw new ServiceException("Cannot get company with id: " + companyId + ". ID doesn't exist");
	}

	public Company getOneCompanyWithCoupons(int companyId) throws ServiceException {
		Company company;
		try {
			company = getOneCompanyWithoutCoupons(companyId);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		company.setCoupons(couponRepository.findByCompanyId(companyId));
		return company;
	}

	public Customer addCustomer(CustomerPayload customer) throws ServiceException {
		String email = customer.getEmail();
		if (customerRepository.existsByEmail(email)) {
			throw new ServiceException("Cannot add customer. Email: \"" + email + "\" already taken.");
		}
//		customer.setCoupons(null);  // Technically unnecessary since in a real scenario you would not be able to add
									// a customer with ready coupons. Only one with an empty list.
									// Here however it is a possible bug, so why not handle it :)
		Customer customerInUse = new Customer();
		customerInUse.setEmail(email);
		customerInUse.setFirstName(customer.getFirstName());
		customerInUse.setLastName(customer.getLastName());
		customerInUse.setPassword(customer.getPassword());
		return customerRepository.save(customerInUse);
	}
//	public Customer addCustomer(Customer customer) throws ServiceException {
//		String email = customer.getEmail();
//		if (customerRepository.existsByEmail(email)) {
//			throw new ServiceException("Cannot add customer. Email: \"" + email + "\" already taken.");
//		}
//		customer.setCoupons(null);  // Technically unnecessary since in a real scenario you would not be able to add
//		// a customer with ready coupons. Only one with an empty list.
//		// Here however it is a possible bug, so why not handle it :)
//		return customerRepository.save(customer);
//	}

	public Customer updateCustomer(Customer customer) throws ServiceException {
		int id = customer.getId();
		String email = customer.getEmail();
		Optional<Customer> opt = customerRepository.findById(id);
		if (opt.isPresent()) {
			if (!customerRepository.existsByEmailAndIdNot(email, id)) {
			Customer customerFromDb = opt.get();
			customerFromDb.setFirstName(customer.getFirstName());
			customerFromDb.setLastName(customer.getLastName());
			customerFromDb.setEmail(email);
			customerFromDb.setPassword(customer.getPassword());
			return customerFromDb;
			}
			else {
				throw new ServiceException("Cannot update customer. Email: \"" + email + "\" already taken.");
			}
		}
		throw new ServiceException("Cannot update customer. id: " + id + " doesn't exist.");
	}

	public void deleteCustomer(int customerId) throws ServiceException {
		if (customerRepository.deleteByIdIs(customerId) == 0) {
			throw new ServiceException("Cannot delete customer with id: " + customerId + ". ID Doesn't exist");
		}
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	public List<Customer> getAllCustomersWithCoupons() {
		List<Customer> customers = getAllCustomers();
		for (Customer customer : customers) {
			if (customer != null) {
				customer.setCoupons(couponRepository.findByCustomersId(customer.getId()));
			}
		}
		return customers;
	}

	public List<Integer> getAllCustomersIds() {
		List<Customer> customers = getAllCustomers();
		List<Integer> customersIds = new ArrayList<>();
		for (Customer customer : customers) {
			customersIds.add(customer.getId());
		}
		return customersIds;
	}

	public Customer getOneCustomerWithoutCoupons(int customerId) throws ServiceException {
		Optional<Customer> opt = customerRepository.findById(customerId);
		if (opt.isPresent()) {
			Customer customer = opt.get();
			return customer;
		}
		throw new ServiceException("Cannot get customer with id: " + customerId + ". ID doesn't exist");
	}

	public Customer getOneCustomerWithCoupons(int customerId) throws ServiceException {
		Customer customer;
		try {
			customer = getOneCustomerWithoutCoupons(customerId);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage());
		}
		customer.setCoupons(couponRepository.findByCustomersId(customerId));
		return customer;
	}

	public Coupon getOneCoupon(int couponId) throws ServiceException {
		Optional<Coupon> opt = couponRepository.findById(couponId);
		if (opt.isPresent()) {
			Coupon coupon = opt.get();
			return coupon;
		}
		throw new ServiceException("Cannot get coupon with id: " + couponId + ". ID doesn't exist");
	}
	
	public List<Coupon> getAllCoupons(){
		return couponRepository.findAll();
	}

}
