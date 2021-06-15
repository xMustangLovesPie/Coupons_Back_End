package app.core.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.entities.Coupon.Category;
import app.core.entities.Customer;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	public List<Coupon> findByCustomersId(int id);
	
	public List<Coupon> findByCustomers(Customer customer);

	public List<Coupon> findByCustomersAndCategory(Customer customer, Category category);

	public List<Coupon> findByCustomersAndPriceLessThan(Customer customer, double price);

	public List<Coupon> findByCompanyId(int id);

	public List<Coupon> findByCompany(Company company);

	public List<Coupon> findByCompanyAndCategory(Company company, Category category);

	public List<Coupon> findByCompanyAndPriceLessThan(Company company, double price);

	public boolean existsByIdNotAndCompanyAndTitle(int id, Company company, String title);
	
	public boolean existsByCompanyAndTitle(Company company, String title);

	public boolean existsByAmountGreaterThanAndId(int amount, int id);

	public boolean existsByEndDateBeforeAndId(LocalDate date, int id);
	
	public List<Coupon> deleteByEndDateIsBefore(LocalDate date);

	public int deleteByIdIsAndCompanyIs(int id, Company company);
	
	
	/**
	 * Finds all the unporchased coupons by this customer (Amount can be 0, can be
	 * out of date)
	 * 
	 * @param customer
	 * @return a list of coupons
	 * 
	 * @author Noam Gonopolski
	 */
	@Query(value = "select * from coupon cou left join "
			+ "(select cou.id from coupon cou left join coupons_vs_customers cvc "
			+ "on cou.id = cvc.coupon_id where cvc.customer_id =?) result on "
			+ "cou.id = result.id where result.id is null", nativeQuery = true)
	public List<Coupon> getUnporchasedCoupons(Customer customer);

	/**
	 * Finds all the purchasable coupons by this customer (Amount has to be greater than 0, coupon
	 * has to be in date)
	 * 
	 * @param customer
	 * @return a list of coupons.
	 * 
	 * @author Noam Gonopolski
	 */
	@Query(value = "select * from coupon cou left join "
			+ "(select cou.id from coupon cou left join coupons_vs_customers cvc "
			+ "on cou.id = cvc.coupon_id where cvc.customer_id =?) result on "
			+ "cou.id = result.id where result.id is null "
			+ "&& cou.amount>0 && cou.end_date>now() && cou.start_date<now()", nativeQuery = true)
	public List<Coupon> getPurchasableCoupons(Customer customer);

}
