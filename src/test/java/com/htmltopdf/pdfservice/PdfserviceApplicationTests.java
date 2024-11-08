package com.htmltopdf.pdfservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import com.htmltopdf.pdfservice.model.PdfCount;
import com.htmltopdf.pdfservice.repo.PdfRetryRepo;
import com.htmltopdf.pdfservice.service.EmailService;
import com.htmltopdf.pdfservice.service.PdfServiceImpl;

@SpringBootTest
  public class PdfserviceApplicationTests {
 
	
	@Mock
    private PdfRetryRepo pdfRetryRepo;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PdfServiceImpl pdfService;   

    private final String binsvcUrl = "http://bin-svc-dev.highwire.org/entity/6e9838c58dd8e6f8/42a6c7d3caa385243cb292f8baa90e157546f074ae496e5214613f36db532331";
    private final String siteId = "scolaris-mheaeworks_krishna";
    private final String atomPath = "/mheaeworks/book/9781260132274/toc-chapter/chapter7/section/section3.atom";
    private final int maxRetryCount = 3;  

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(pdfService, "htmlpdfSvc", "http://html-pdf-svc-dev.highwire.org/create");
        ReflectionTestUtils.setField(pdfService, "maxRetryCount", 3);
    }

    @Test
    public void testProcessPdfNoExistingRetryCountInsertsNewEntryAndCallsService() {
        // Arrange
        when(pdfRetryRepo.findByAtomPath(atomPath)).thenReturn(null);

        // Act
        pdfService.processPdf(binsvcUrl, atomPath, siteId);

        // Assert
        verify(pdfRetryRepo).findByAtomPath(atomPath);
        verify(pdfRetryRepo).save(any(PdfCount.class));   
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

	
	  @Test
	  public void testProcessPdfRetryCountBelowMaxUpdatesCountAndCallsService() { 
		  // Arrange
	  PdfCount retryCount = new PdfCount(2, binsvcUrl, atomPath); 
	  // Retry count below max 
	  when(pdfRetryRepo.findByAtomPath(atomPath)).thenReturn(retryCount);
	  
	  // Act 
	  pdfService.processPdf(binsvcUrl, atomPath, siteId);
	  
	  // Assert 
	  verify(pdfRetryRepo).findByAtomPath(atomPath);
	  verify(pdfRetryRepo).saveAndFlush(retryCount); 
	  
	  // Verify the retry count is updated 
	  verify(emailService, never()).sendSimpleEmail(anyString(),
	  anyString(), anyString()); }

	@Test 
	public void testProcessPdfRetryCountAtMaxSendsEmail() {
		// Arrange
	  PdfCount retryCount = new PdfCount(maxRetryCount, binsvcUrl, atomPath); 
	 
	  // Retry count at max
	  when(pdfRetryRepo.findByAtomPath(atomPath)).thenReturn(retryCount);
	  
	  // Act 
	  pdfService.processPdf(binsvcUrl, atomPath, siteId);
	  
	  // Assert 
	  verify(pdfRetryRepo).findByAtomPath(atomPath); 
	  verify(pdfRetryRepo,never()).save(any(PdfCount.class)); 
	  verify(pdfRetryRepo,never()).saveAndFlush(any(PdfCount.class));
	  verify(emailService).sendSimpleEmail("pooja.tomar@highwirepress.com",
	  "Maximum Retry Count Reached for PDF Generation", "Please check"); }
	 
	
}
