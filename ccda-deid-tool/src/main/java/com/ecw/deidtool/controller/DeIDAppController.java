package com.ecw.deidtool.controller;

import com.ecw.deidtool.interfaces.DeIDFileService;
import com.ecw.deidtool.interfaces.StorageService;
import com.ecw.deidtool.storage.StorageException;
import com.ecw.deidtool.storage.StorageFileNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Controller
//@RequestMapping("/deid-tool")
@RequestMapping("${suite.deid.base-path:/deid-tool}")
public class DeIDAppController {

	private final StorageService storageService;
	private final DeIDFileService deIDFileService;

	private void init(StorageService storageService) {
//			storageService.deleteAll();
			storageService.init();
	}

	public DeIDAppController(StorageService storageService, DeIDFileService deIDServiceFile) {
		this.storageService = storageService;
		this.deIDFileService = deIDServiceFile;
	}


	@GetMapping({"", "/"})
	public String showHomePage(Model model, HttpServletRequest request, HttpSession session) {

		log.debug("Application Context: " + request.getContextPath());
		init(storageService);
		if(Objects.isNull(request.getUserPrincipal()))
			storageService.createUserDIR(request.getSession().getId());
		else
			storageService.createUserDIR(request.getUserPrincipal().getName());
		model.addAttribute("sessionID", request.getSession().getId());
		return "home";
	}

	@GetMapping("/loadFileForUser")
	public String showExistingDeIDFileForUser(Model model, HttpServletRequest request) {
		log.debug("Application Context: " + request.getContextPath());
		model.addAttribute("files", storageService.loadAllForUser().map(
						path -> MvcUriComponentsBuilder.fromMethodName(DeIDAppController.class,
								"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));
		return "response :: deid-ccda";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		log.debug("serveFile(filename): " + filename);
		Resource file = storageService.loadAsResource(filename);

		if (file == null)
			return ResponseEntity.notFound().build();
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + file.getFilename() + "\"").body(file);
	}

	//TODO: Explore usage of DTO when form is submitted via AJAX
	@PostMapping({"", "/"})
	public String processFile(@RequestParam("file") MultipartFile file, Model model,
							  @RequestParam("categories") List<String> categories) throws InterruptedException {

		storageService.emptyUserDir();

		if(file.isEmpty() || null == file.getContentType() || file.getOriginalFilename().length() <=0){
			model.addAttribute("userFeedback", "Please upload a file");
			return "response :: deid-ccda";
		}
		log.info("Submitting C-CDA XML File to de-id service...");
		boolean isCCDADeID = deIDFileService.deidentifyCCDA(file, categories);
		if(!isCCDADeID){
			model.addAttribute("userFeedback", "No patient identifiers found!");
			return "response :: deid-ccda";
		}

		log.debug("files attr: " + storageService.loadAllForUser().map(
						path -> MvcUriComponentsBuilder.fromMethodName(DeIDAppController.class,
								"serveFile", path.getFileName().toString()).build().toUri().toString()));

		model.addAttribute("files", storageService.loadAllForUser().map(
						path -> MvcUriComponentsBuilder.fromMethodName(DeIDAppController.class,
								"serveFile", path.getFileName().toString()).build().toUri().toString())
				.collect(Collectors.toList()));
		return "response :: deid-ccda";
	}



	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc, Model model) {
		log.debug("Entered handleStorageFileNotFound handler method");
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(StorageException.class)
	public ResponseEntity<?> handleStorageError(StorageException exc, Model model) {
		log.debug("Entered handleStorageError handler method");
		return ResponseEntity.internalServerError().build();
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<?> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exc, Model model) {
		log.debug("Entered handleMaxUploadSizeExceeded handler method");
		return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
	}

}
