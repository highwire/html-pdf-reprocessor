package org.highwire.htmlpdf.reprocessor.controller;

 
 import java.io.IOException;

import org.highwire.htmlpdf.reprocessor.service.HtmlPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

 
@RestController
@CrossOrigin
public class HtmlPdfController {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlPdfController.class);
	
	@Autowired
     private HtmlPdfService pdfService;
	 
	@Autowired
    KafkaTemplate<String, String> kafkaTemplate; 
  
        
    
    //For testing purpose
     @GetMapping("isPdfBlank") 
     public String checkPdfIsBlankOrNot() throws IOException 
     { 
    	 String siteId="scolaris-mheaeworks_blue";
    	 String atomPath="mheaeworks/book/9781260132274/toc-chapter/chapter7/section/section3.atom";
    	 String binsvcUrl="http://bin-svc-dev.highwire.org/entity/6e9838c58dd8e6f8/42a6c7d3caa385243cb292f8baa90e157546f074ae496e5214613f36db532331";
    	    Boolean isBlank=pdfService.readPdfUrl(binsvcUrl);
    	     if(isBlank) {
    	 //  	 pdfService.processPdf(binsvcUrl,atomPath,siteId); 
    	     }else {
    	    	 return "Pdf is Perfectly fine."; 
    	     }
         return ""; 
     } 
	
	
}
