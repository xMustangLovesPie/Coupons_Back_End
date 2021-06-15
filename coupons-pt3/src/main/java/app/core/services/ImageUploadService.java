package app.core.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {
	@Value("${file.upload-dir}")
	private String storagePath;
	private Path fileStoragePath;

	@PostConstruct
	public void init() {
		fileStoragePath = Paths.get(storagePath).toAbsolutePath();
		System.out.println(fileStoragePath);

		try {
			Files.createDirectories(fileStoragePath);
		} catch (IOException e) {
			throw new RuntimeException("Cannot create directory ", e);
		}
	}

	public String storeFile(MultipartFile file, String name) {
		String fileName = file.getOriginalFilename();
		String extention = name.substring(name.lastIndexOf("."));
		if(name.contains("..")) {
			throw new RuntimeException("Illegal name");
		} else {
			if (!extention.equalsIgnoreCase(".jpg") && !extention.equalsIgnoreCase(".png")) {
				throw new RuntimeException("Unsupported file format. Please use .png or .jpg");
			}
		}
		String fileNameNoExtention = name.substring(0, name.lastIndexOf("."));
		String fileNameJpg = fileNameNoExtention+".jpg";
		String fileNamePng = fileNameNoExtention+".png";
		Path targetLocation = fileStoragePath.resolve(name);
		Path jpgPath = fileStoragePath.resolve(fileNameJpg);
		Path pngPath = fileStoragePath.resolve(fileNamePng);
		try {
			Files.deleteIfExists(jpgPath);
			Files.deleteIfExists(pngPath);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return "pics/" + name;
		} catch (IOException e) {
			throw new RuntimeException("storing file " + fileName + " failed", e);
		}
	}
	
	public void DeleteFile(String name) {
		String fileNameJpg = name + ".jpg";
		String fileNamePng = name + ".png";
		Path jpgPath = fileStoragePath.resolve(fileNameJpg);
		Path pngPath = fileStoragePath.resolve(fileNamePng);
		try {
			Files.deleteIfExists(jpgPath);
			Files.deleteIfExists(pngPath);
		} catch (IOException e) {
			throw new RuntimeException("deleting file " + name + " failed", e);
		}
	}
}
