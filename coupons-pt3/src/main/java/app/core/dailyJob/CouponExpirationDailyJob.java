package app.core.dailyJob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import app.core.services.DailyJobService;

/**
 * The daily job task that goes over the coupons daily to check if any coupons
 * have expired
 * 
 * This is a scheduled task that runs once at midnight every day using cron, set
 * to once every day of the week at exactly midnight. 
 * 
 * @author Noam Gonopolski
 */

@Component
public class CouponExpirationDailyJob {

	@Autowired
	private DailyJobService jobService;

	@Scheduled(cron = "${job.service.cron}")
	public void deleteCoupons() {
		System.out.println("Coupon removal service started!");
		System.out.println("Removing expired coupons..");
		System.out.println("Done! " + jobService.removeExpiredCoupons() + " expired coupons removed");
	}
}