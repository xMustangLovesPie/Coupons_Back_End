package app.core.controllers;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Coupon;
import app.core.entities.Coupon.Category;
import app.core.exceptions.ServiceException;
import app.core.jwt.JwtUtil;
import app.core.services.CustomerService;

@CrossOrigin
@RestController
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private JwtUtil tokenUtil;

	private boolean test(String token) {
		Date expiration = tokenUtil.extractExpiration(token);
		if (expiration.before(Date.from(Instant.now()))) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
		}
		return true;
	}

	@PostMapping("/api/customer/coupon/{id}")
	public void purchaseCoupon(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			String email = tokenUtil.extractUsername(token);
			customerService.purchaseCoupon(email, id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/customer/coupon/get-purchasable/")
	public List<Coupon> getPurchasableCoupons(@RequestHeader String token) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return customerService.getPurchasableCoupons(email);
	}

	@GetMapping("/api/customer/coupon/")
	public List<Coupon> getAllCoupons(@RequestHeader String token) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return customerService.getAllCoupons(email);
	}

	@GetMapping("/api/customer/coupon/category/{category}")
	public List<Coupon> getAllCoupons(@RequestHeader String token, @PathVariable Category category) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return customerService.getAllCoupons(email, category);
	}

	@GetMapping("/api/customer/coupon/price/{price}")
	public List<Coupon> getAllCoupons(@RequestHeader String token, @PathVariable double price) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return customerService.getAllCoupons(email, price);
	}
}
