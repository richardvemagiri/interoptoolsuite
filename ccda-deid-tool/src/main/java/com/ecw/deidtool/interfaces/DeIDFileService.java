package com.ecw.deidtool.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DeIDFileService {

    boolean deidentifyCCDA(MultipartFile file, List<String> categories);
}