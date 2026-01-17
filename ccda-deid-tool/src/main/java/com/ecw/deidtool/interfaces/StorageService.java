package com.ecw.deidtool.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

	void init();

//	Stream<Path> loadAll();

	Stream<Path> loadAllForUser();

	Path load(String filename);

	Resource loadAsResource(String filename);


	void deleteAll();

	void emptyUserDir();

	void createUserDIR(String userID);

	void store(MultipartFile file, MultipartFile deIDfile);
}
