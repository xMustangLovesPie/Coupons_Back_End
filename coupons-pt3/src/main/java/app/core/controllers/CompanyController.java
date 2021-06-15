package app.core.controllers;

import java.time.Instant;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import app.core.entities.Coupon;
import app.core.entities.Coupon.Category;
import app.core.exceptions.ServiceException;
import app.core.jwt.JwtUtil;
import app.core.payloads.CouponPayload;
import app.core.services.CompanyService;
import app.core.services.ImageUploadService;

@CrossOrigin
@RestController
public class CompanyController {

	@Autowired
	private CompanyService companyService;
	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired
	private JwtUtil tokenUtil;

	private boolean test(String token) {
		Date expiration = tokenUtil.extractExpiration(token);
		if (expiration.before(Date.from(Instant.now()))) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session expired");
		}
		return true;
	}

	@PostMapping("/api/company/coupon/")
	public Coupon addCoupon(@RequestHeader String token, @ModelAttribute CouponPayload coupon) {
		test(token);
		try {
			MultipartFile file = coupon.getImage();
			String fileName = file.getOriginalFilename();
			String email = tokenUtil.extractUsername(token);
			Coupon couponToReturn = companyService.addCoupon(email, coupon);
			imageUploadService.storeFile(coupon.getImage(), couponToReturn.getId()+fileName.substring(fileName.lastIndexOf(".")));
			return couponToReturn;
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
	}

	@GetMapping("/api/company/get-coupon/{id}")
	public Coupon getCoupon(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			String email = tokenUtil.extractUsername(token);
			return companyService.getOneCoupon(email, id);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PutMapping("/api/company/coupon/")
	public Coupon updateCoupon(@RequestHeader String token, @ModelAttribute CouponPayload coupon) {
		test(token);
		try {
			String email = tokenUtil.extractUsername(token);
			MultipartFile file = coupon.getImage();
			String fileName = file.getOriginalFilename();
			imageUploadService.storeFile(coupon.getImage(), coupon.getId()+fileName.substring(fileName.lastIndexOf(".")));
			return companyService.updateCoupon(email, coupon);
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@DeleteMapping("/api/company/coupon/{id}")
	public void deleteCoupon(@RequestHeader String token, @PathVariable int id) {
		test(token);
		try {
			String email = tokenUtil.extractUsername(token);
			companyService.deleteCoupon(email, id);
			imageUploadService.DeleteFile(Integer.toString(id));
		} catch (ServiceException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/api/company/coupon/")
	public List<Coupon> getAllCoupons(@RequestHeader String token) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return companyService.getAllCoupons(email);
	}
		
	@GetMapping("/api/company/coupon/category/{category}")
	public List<Coupon> getAllCoupons(@RequestHeader String token, @PathVariable Category category) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return companyService.getAllCoupons(email, category);
	}

	@GetMapping("/api/company/coupon/price/{price}")
	public List<Coupon> getAllCoupons(@RequestHeader String token, @PathVariable double price) {
		test(token);
		String email = tokenUtil.extractUsername(token);
		return companyService.getAllCoupons(email, price);
	}

}
