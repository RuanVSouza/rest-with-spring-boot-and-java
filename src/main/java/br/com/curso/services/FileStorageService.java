package br.com.curso.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.curso.config.FileStorageConfig;
import br.com.curso.exceptions.FileStorageException;
import br.com.curso.exceptions.MyFileNotFoundException;


@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUploadDir())
				.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			Files.createDirectories(this.fileStorageLocation);
			} catch (Exception e) {
			throw new FileStorageException("Could not create the directory where the upload files will be stored@!", e);		
			}
	}
	
	public String StoreFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		try {
			if(fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence" + fileName);
				}
			
			Path targetLocation = this.fileStorageLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);			
			
			return fileName;
			
		} catch (Exception e) {
			throw new FileStorageException("Could not store file "+ fileName + ". will be stored@!", e);
		}
	}
	
	public Resource loadFilesAsResource(String filename) {
		try {
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			
			if(resource.exists()) return resource;
			else throw new MyFileNotFoundException("file not found ");
			
			
		} catch (Exception e) {
			throw new MyFileNotFoundException("file not found "+ filename, e);
		}
		
		
	}
	
	
}
