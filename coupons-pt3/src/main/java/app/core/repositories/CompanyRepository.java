package app.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import app.core.entities.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer>{
	
	public Company findOneByEmail(String email);
	
	public Company findOneByEmailAndPassword(String email, String password);
	
	public boolean existsByNameOrEmail(String name, String email);
	
	public boolean existsByEmailAndIdNot(String email, int id);
	
	public int deleteByIdIs(int id);
	
}
