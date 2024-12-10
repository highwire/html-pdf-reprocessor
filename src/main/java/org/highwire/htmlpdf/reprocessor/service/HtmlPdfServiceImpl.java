package org.highwire.htmlpdf.reprocessor.service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.highwire.htmlpdf.reprocessor.controller.HtmlPdfController;
import org.highwire.htmlpdf.reprocessor.entity.PdfCount;
import org.highwire.htmlpdf.reprocessor.repository.HtmlPdfRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;

@Service
public class HtmlPdfServiceImpl implements HtmlPdfService{
 
	@Autowired
	private HtmlPdfRepository pdfRetryRepo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlPdfServiceImpl.class);

	  @Value("${pdf.max.retry}")
	  private Integer maxRetryCount;
	  
	  @Value("${html.pdf.svc}")
	  private String htmlpdfSvc;
	  
	  @Value("${pdf.white.percentage}")
	  private Double pdfWhitePercentage;
	  
	  @Value("${pixel.value}")
	  private Integer pixelValue;
	
	  @Autowired
	  private HtmlPdfEmailService emailService;
	  
	@Override
	public Boolean readPdfUrl(String binsvcUrl) throws IOException {
		LOGGER.debug("Inside readPdfUrl");
		boolean isBlank=false;
		try {
            InputStream pdfStream =  fetchPDF(binsvcUrl);
              isBlank =  isPageMostlyWhite(pdfStream);
            if (isBlank) {
            	LOGGER.warn("The first page of the PDF is blank or mostly white/gray.");
             } else {
            	 LOGGER.warn("The first page of the PDF contains content.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		return isBlank;
		
	}

	public InputStream fetchPDF(String pdfUrl) throws IOException {
		LOGGER.debug("Inside fetchPDF");
        URL url = new URL(pdfUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == 200) {
            return connection.getInputStream();
        } else {
            throw new IOException("Failed to fetch PDF, response code: " + connection.getResponseCode());
        }
    }
	
	public boolean isPageMostlyWhite(InputStream pdfStream) throws IOException {
		LOGGER.debug("Inside isPageMostlyWhite");
        PDDocument document = PDDocument.load(pdfStream);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
         // Render the first page to an image
        BufferedImage firstPageImage = pdfRenderer.renderImage(0);
         int whitePixelCount = 0;
        int totalPixelCount = firstPageImage.getWidth() * firstPageImage.getHeight();
         for (int y = 0; y < firstPageImage.getHeight(); y++) {
            for (int x = 0; x < firstPageImage.getWidth(); x++) {
                Color color = new Color(firstPageImage.getRGB(x, y));
                int r = color.getRed();
                int g = color.getGreen();
                int b = color.getBlue();

                // Check if the pixel is white or gray  
                if (r > pixelValue && g > pixelValue && b > pixelValue) {
                    whitePixelCount++;
                }
            }
        }
         document.close();
         double whitePercentage = (whitePixelCount / (double) totalPixelCount) * 100;
        System.out.println("White/Gray Percentage: " + whitePercentage);
        LOGGER.info("White/Gray Percentage: " + whitePercentage);
         return whitePercentage > pdfWhitePercentage;
    }

	 
	public void processPdf(String binsvcUrl,String atomPath,String siteId) {
		LOGGER.debug("Inside processPdf");
		 PdfCount retryCount = pdfRetryRepo.findByAtomPath(atomPath);
		 if (retryCount == null) {
			//call html to pdf service
			 callHtmlToPdfService(siteId,atomPath);
			 LOGGER.info("No record found for URL. Inserting new entry with retry count 1.");
			 pdfRetryRepo.save(new PdfCount(1, binsvcUrl,atomPath));
         } else  {
              if (retryCount.getCount() < maxRetryCount) {
                    
                   //call html to pdf service
                   callHtmlToPdfService(siteId,atomPath);
                   
                   int newRetryCount = retryCount.getCount() + 1;
                   LOGGER.info("Record found. Updating retry count to " + newRetryCount);
                   retryCount.setCount(newRetryCount);
                   pdfRetryRepo.saveAndFlush(retryCount);
                   
              } else {
                  
            	  LOGGER.info("Max retry count reached for PDF URL: " + binsvcUrl);
            	  String toEmail="pooja.tomar@highwirepress.com";
            	  String subject="Maximum Retry Count Reached for PDF Generation";
            	  String body="Please check";
            	  emailService.sendSimpleEmail(toEmail, subject, body);
             }
         }
	}

	private void callHtmlToPdfService(String siteId, String atomPath) {
		  LOGGER.debug("Inside callHtmlToPdfService");
  		   String url = UriComponentsBuilder.fromHttpUrl(htmlpdfSvc)
                   .queryParam("site", siteId)
                   .queryParam("src", atomPath)
                   .toUriString();
  		   try {
 		   ResponseEntity<String> response =
 			        new RestTemplate().postForEntity(url,null, String.class);
    		   } catch (Exception e) {
   		        LOGGER.error("An error occurred while calling HTML to PDF service: {}", e.getMessage());
  		        e.printStackTrace();
  		    }
 	}

	@Override
	public String[] parseValues(String value) {
		 LOGGER.debug("Inside parseValues");
		try {
	        ObjectMapper objectMapper = new ObjectMapper();
 	        JsonNode rootNode = objectMapper.readTree(value);
 	        String apath = rootNode.get("apath").asText();
	        String binsvcUri = rootNode.get("binsvc-uri").asText();
	        String siteId = rootNode.get("site_id").asText();

 	        return new String[] { apath, binsvcUri, siteId };

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	   
	    return new String[0];
	}   
	 
}
