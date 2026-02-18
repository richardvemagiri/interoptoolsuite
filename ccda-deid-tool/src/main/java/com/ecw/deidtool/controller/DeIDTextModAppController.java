package com.ecw.deidtool.controller;

import com.ecw.deidtool.interfaces.DeIDTextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/deid-tool")
public class DeIDTextModAppController {

	private final DeIDTextService deIDTextService;

	public DeIDTextModAppController(DeIDTextService deIDTextService) {
		this.deIDTextService = deIDTextService;
	}

	@PostMapping("/textmod")
	public String removePII(@RequestParam("ccdaXML") String ccdaXML,
							@RequestParam("categories") List<String> categories,
							Model model) throws InterruptedException {
		log.info("Submitting C-CDA XML Text to deid-service...");
		String ccdaOutput = deIDTextService.deidentifyCCDAXMLText(ccdaXML, categories);
		if(ccdaOutput==null){
			model.addAttribute("userFeedbackForText", "Error occurred! Try again.");
			return "response :: deid-ccda-text";
		}

		if(ccdaOutput.isBlank()){
			model.addAttribute("userFeedbackForText", "No patient identifiers found!");
			return "response :: deid-ccda-text";
		}


		model.addAttribute("ccdaOutput" , ccdaOutput);
		return "response :: deid-ccda-text";
	}



}
