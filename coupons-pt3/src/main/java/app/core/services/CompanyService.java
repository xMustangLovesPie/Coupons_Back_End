package app.core.services;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import app.core.entities.Company;
import app.core.entities.Coupon;
import app.core.entities.Coupon.Category;
import app.core.exceptions.ServiceException;
import app.core.payloads.CouponPayload;

/**
 * Service class for the company.
 * 
 * @author Noam Gonopolski
 */
@Service
@Transactional
//@Scope(BeanDefinition.SCOPE_PROTOTYPE)

public class CompanyService extends GeneralService {

	private DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private Company retrieveFromUsername(String email) {
		return companyRepository.findOneByEmail(email);
	}
	
	private Company retrieveFromId(int id) {
		return companyRepository.getOne(id);
	}

	public Company login(String email, String password) {
		return companyRepository.findOneByEmailAndPassword(email, password);
	}

	public Coupon addCoupon(String email, CouponPayload coupon) throws ServiceException {
//		coupon.setCustomers(null);
		Company company = retrieveFromUsername(email);
		String title = coupon.getTitle();
		if (couponRepository.existsByCompanyAndTitle(company, title)) {
			throw new ServiceException("Cannot add coupon. Title: \"" + title + "\" already exists.");
		}
		Optional<Company> opt = companyRepository.findById(company.getId());
		if (opt.isPresent()) {
			Coupon couponInUse = new Coupon();
			couponInUse.setCompany(opt.get());
			couponInUse.setTitle(coupon.getTitle());
			couponInUse.setCategory(Category.valueOf(coupon.getCategory()));
			couponInUse.setDescription(coupon.getDescription());
			couponInUse.setAmount(Integer.parseInt(coupon.getAmount()));
			couponInUse.setPrice(Double.parseDouble(coupon.getPrice()));
			couponInUse.setStartDate(LocalDate.parse(coupon.getStartDate(), dateformatter));
			couponInUse.setEndDate(LocalDate.parse(coupon.getEndDate(), dateformatter));
			return couponRepository.save(couponInUse);
		}
		// shouldn't get here if the company exists which it should unless db was
		// manipulated by hand
		throw new ServiceException(
				"Unexpected db manipulation mid-method execution. Did the server crash or did you somehow manage to delete the company in the middle of execution?");
	}

	public Coupon getOneCoupon(String email, int couponId) throws ServiceException {
		Company company = retrieveFromUsername(email);
		Optional<Coupon> opt = couponRepository.findById(couponId);
		if (opt.isPresent()) {
			Coupon coupon = opt.get();
			if (coupon.getCompany().getId() == company.getId()) {
				return coupon;
			} else
				throw new ServiceException(
						"Cannot get coupon with id: " + couponId + ". ID doesn't exist for this company");
		}
		throw new ServiceException("Cannot get coupon with id: " + couponId + ". ID doesn't exist");
	}

	public Coupon updateCoupon(String email, CouponPayload coupon) throws ServiceException {
		int id = coupon.getId();
		Coupon couponFromDb = getOneCoupon(email, id);
		String title = coupon.getTitle();
		if (couponRepository.existsByIdNotAndCompanyAndTitle(id, couponFromDb.getCompany(), title)) {
			throw new ServiceException("Can't update coupon. Title: \"" + title + "\" already exists for this company");
		}

		try {
			couponFromDb.setTitle(title);
			couponFromDb.setAmount(Integer.parseInt(coupon.getAmount()));
			couponFromDb.setCategory(Category.valueOf(coupon.getCategory()));
			couponFromDb.setDescription(coupon.getDescription());
			couponFromDb.setEndDate(LocalDate.parse(coupon.getEndDate(), dateformatter));
			couponFromDb.setStartDate(LocalDate.parse(coupon.getStartDate(), dateformatter));
			couponFromDb.setPrice(Double.parseDouble(coupon.getPrice()));
			return couponFromDb;
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public void deleteCoupon(String email, int couponId) throws ServiceException {
		if (couponRepository.deleteByIdIsAndCompanyIs(couponId, retrieveFromUsername(email)) == 0)
			throw new ServiceException("Cannot delete coupon with id: " + couponId + ". ID Doesn't exist");
	}

//	public List<CouponPayload> getAllCoupons(String email) {
//		List<Coupon> coupons = couponRepository.findByCompany(retrieveFromUsername(email));
//		List<CouponPayload> wrappedCoupons = new ArrayList<>();
//		for (Coupon coupon : coupons) {
//			CouponPayload frontEndCoupon = new CouponPayload();
//			frontEndCoupon.setTitle(coupon.getTitle());
//			frontEndCoupon.setCategory(coupon.getCategory().toString());
//			frontEndCoupon.setDescription(coupon.getDescription());
//			frontEndCoupon.setAmount(Integer.toString(coupon.getAmount()));
//			frontEndCoupon.setPrice(Double.toString((coupon.getPrice())));
//			frontEndCoupon.setStartDate(coupon.getStartDate().toString());
//			frontEndCoupon.setEndDate(coupon.getEndDate().toString());
//			try {
//			String fileNameJpg = coupon.getId()+".jpg";
//			String fileNamePng = coupon.getId()+".png";
//			File fileJpg = new File("src/main/resources/static/pics/"+coupon.getId()+".jpg");
//			File filePng = new File("src/main/resources/static/pics/"+coupon.getId()+".png");
//		    FileInputStream inputStream =  new FileInputStream(fileJpg);
//			MockMultipartFile multiPart = new MockMultipartFile(fileNameJpg, fileNameJpg, "image/jpeg", inputStream);
//			frontEndCoupon.setImage(multiPart);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			wrappedCoupons.add(frontEndCoupon);
//		}
//		return wrappedCoupons;
//	}

	public List<Coupon> getAllCoupons(String email) {
		List<Coupon> coupons = couponRepository.findByCompany(retrieveFromUsername(email));
		for (Coupon coupon : coupons) {
			File file = new File("src/main/resources/static/pics/"+coupon.getId()+".jpg");
			if(file.exists()) {
				coupon.setImageName(coupon.getId()+".jpg");
			} else {
				coupon.setImageName(coupon.getId()+".png");
			}
		}
		return coupons;
	}


	public List<Coupon> getAllCoupons(String email, Category category) {
		return couponRepository.findByCompanyAndCategory(retrieveFromUsername(email), category);
	}

	public List<Coupon> getAllCoupons(String email, double maxPrice) {
		return couponRepository.findByCompanyAndPriceLessThan(retrieveFromUsername(email), maxPrice);
	}

	public Company getDetails(String email) {
		return retrieveFromUsername(email);
	}
	
	public Company getDetails(int id) {
		return retrieveFromId(id);
	}
}