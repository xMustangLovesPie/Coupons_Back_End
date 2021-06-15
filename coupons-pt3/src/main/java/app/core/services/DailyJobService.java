package app.core.services;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.core.entities.Coupon;

/**
 * 
 * Service class for the daily job.
 * Is responsible for deleting all the coupons which have an end date before now,
 * via the {@link removeExpiredCoupons} method, which uses JPA
 * 
 * @author Noam Gonopolski
 */
@Service
@Transactional
public class DailyJobService extends GeneralService{
	
	@Autowired
	private ImageUploadService imageUploadService;
	
	public int removeExpiredCoupons() {
		List<Coupon> coupons = couponRepository.deleteByEndDateIsBefore(LocalDate.now());
		for (Coupon coupon : coupons) {
			imageUploadService.DeleteFile(Integer.toString(coupon.getId()));
		}
		return coupons.size();
	}
}
