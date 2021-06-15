package app.core.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import app.core.entities.Coupon;
import app.core.entities.Coupon.Category;
import app.core.entities.Customer;
import app.core.exceptions.ServiceException;

/**
 * Service class for the customer. 
 * All additional methods (such as {@link #getUnporchasedCoupons} and {@link #getPurchasableCoupons})
 * are added for flexibility, for any future features, and are explained via JavaDoc.
 * 
 * @author Noam Gonopolski
 */
@Service
@Transactional
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)

public class CustomerService extends GeneralService {

//	private Customer customer;
//
//	public void setCustomer(Customer customer) {
//		this.customer = customer;
//	}
//	
//	public Customer getCustomer() {
//		return customer;
//	}
	
	private Customer retrieveFromUsername(String email) {
		return customerRepository.findOneByEmail(email);
	}
	
	public Customer login(String email, String password) {
		return customerRepository.findOneByEmailAndPassword(email, password);
	}

	public void purchaseCoupon(String email, int couponId) throws ServiceException {
		Customer customer = retrieveFromUsername(email);
		if (customerRepository.existsByIdAndCouponsId(customer.getId(), couponId)){
			Coupon coupon = couponRepository.getOne(couponId);
			//Can get the coupon right away without extra tests because we just checked if it exists^
			throw new ServiceException("Purchase coupon failed. You have already purchased coupon titled \"" +coupon.getTitle()+ "\" from company "+coupon.getCompany().getName());
		}
		Optional<Coupon> opt = couponRepository.findById(couponId);
		if (opt.isPresent()) {
			Coupon coupon = opt.get();
			if (coupon.getEndDate().isBefore(LocalDate.now())) {
				throw new ServiceException("Purchase coupon failed. Coupon with id: "+couponId+" is expired.");
			}
			if (!(coupon.getAmount()>0)) {
				throw new ServiceException("Purchase coupon failed. Coupon with id: "+couponId+" is out of stock.");
			}
//			Customer customer = getCustomerFromDb();
			
			coupon.setAmount(coupon.getAmount() - 1);
			customer.addCoupon(coupon);
		}
		else {
			throw new ServiceException("Purchase coupon failed. Coupon with id: "+couponId+" doesn't exist.");
		}
	}

	public List<Coupon> getAllCoupons(String email) {
		return couponRepository.findByCustomers(retrieveFromUsername(email));
	}

	public List<Coupon> getAllCoupons(String email, Category category) {
		return couponRepository.findByCustomersAndCategory(retrieveFromUsername(email), category);
	}

	public List<Coupon> getAllCoupons(String email, double maxPrice) {
		return couponRepository.findByCustomersAndPriceLessThan(retrieveFromUsername(email), maxPrice);
	}
	
	/**
	 * @return A list of all the coupons that haven't already been purchased by the customer. 
	 * 
	 * @author Noam Gonopolski
	 */
	public List<Coupon> getUnporchasedCoupons(String email){
		return couponRepository.getUnporchasedCoupons(retrieveFromUsername(email));
	}
	/**
	 * @return A list of all the coupons that haven't already been purchased by the customer, 
	 * and are available for purchase, meaning they are in stock, aren't expired, and haven't 
	 * already been purchased.
	 * 
	 * @author Noam Gonopolski
	 */
	public List<Coupon> getPurchasableCoupons(String email){
		return couponRepository.getPurchasableCoupons(retrieveFromUsername(email));
	}
	
	public Customer getDetails(String email) {
		return retrieveFromUsername(email);
	}
}
