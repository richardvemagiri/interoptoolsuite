package com.ecw.deidtool.service;

import com.ecw.deidtool.interfaces.StorageService;
import com.ecw.deidtool.storage.StorageException;
import com.ecw.deidtool.storage.StorageFileNotFoundException;
import com.ecw.deidtool.storage.StorageProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Slf4j
@Getter
@Setter
@Service
public class StorageServiceImpl implements StorageService {

	private final Path rootLocation;
	private Path userRootLocation;

	@Value("#{appProperties.fileNameAppend}")
	private String fileNameAppend;


	@Autowired
	public StorageServiceImpl(StorageProperties properties) {
        if(properties.getLocation().trim().length() == 0){
			log.warn("File upload location can not be empty!");
            throw new StorageException("File upload location can not be Empty.");
        }

		this.rootLocation = Paths.get(properties.getLocation());
		log.debug("rootLocation: " + this.rootLocation);

	}

	public void store(MultipartFile file, MultipartFile deIDfile){

		log.debug("userRootLocation: " + userRootLocation);
		String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
		log.debug("FileName: " + fileName);
		String fileNameExt = FilenameUtils.getExtension(file.getOriginalFilename());
		log.debug("FileName Ext.: " + fileNameExt);


		Path destinationFile = this.userRootLocation.resolve(
						Paths.get(fileName.concat("-").concat(this.fileNameAppend).concat(".").concat(fileNameExt)))
				.normalize().toAbsolutePath();
		if (!destinationFile.getParent().equals(this.userRootLocation.toAbsolutePath())) {
			// This is a security check
			log.error("Cannot store file outside current directory");
			throw new StorageException(
					"Cannot store file outside current directory.");
		}

		try{

//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			Source xmlSource = new DOMSource(document);
//			Result outputTarget = new StreamResult(outputStream);
//			TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
//			InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

			InputStream is =  new BufferedInputStream(deIDfile.getInputStream());

			Files.copy(is, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);

		}catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}


	@Override
	public Stream<Path> loadAllForUser() {
		try {
			log.debug("this.userRootLocation: " + this.userRootLocation);

			Stream<Path> pathStream = Files.walk(this.userRootLocation);
			pathStream
					.filter(path -> !path.equals(this.userRootLocation))
					.forEach(path -> log.debug("loadAllForUser pathStream forEach(): " + path));

			return Files.walk(this.userRootLocation)
					.filter(path -> !path.equals(this.userRootLocation))
					.map(this.userRootLocation::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored user files", e);
		}

	}



	@Override
	public Path load(String filename) {
		return userRootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);

			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
	}

	public void emptyUserDir(){
		try {
			log.debug("Calling FileUtils.cleanDirectory(this.userRootLocation.toFile())");
			FileUtils.cleanDirectory(this.userRootLocation.toFile());
		} catch (IOException e) {
			throw new StorageException("Could not empty user directory", e);
		}
	}

	@Override
	public void createUserDIR(String userID) {
		try {
			this.userRootLocation = this.rootLocation.resolve(userID);
			log.debug("userRootLocation: " + getUserRootLocation());
			Files.createDirectories(this.rootLocation.resolve(userID));
		}
		catch (IOException e) {
			throw new StorageException("Could not create user directory", e);
		}
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);

		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

}
