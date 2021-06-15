package app.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.core.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	public Customer findOneByEmail(String email);
	
	public Customer findOneByEmailAndPassword(String email, String password);
	
	public boolean existsByEmail(String email);
	
	public boolean existsByIdAndCouponsId(int id, int couponId);
	
	public boolean existsByEmailAndIdNot(String email, int id);
	
	public int deleteByIdIs(int id);
}