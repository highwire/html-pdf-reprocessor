package com.htmltopdf.pdfservice.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public interface PdfService {

	Boolean readPdfUrl(String binsvcUrl) throws IOException;

 void processPdf(String binsvcUrl, String atomPath, String siteId);

	String[] parseValues(String value);

	
	
}
