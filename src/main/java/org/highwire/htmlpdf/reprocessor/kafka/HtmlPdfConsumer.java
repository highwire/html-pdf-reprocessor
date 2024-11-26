package org.highwire.htmlpdf.reprocessor.kafka;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.highwire.htmlpdf.reprocessor.service.HtmlPdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
 public class HtmlPdfConsumer {

    private static final Logger logger = LoggerFactory.getLogger(HtmlPdfConsumer.class);

    @Autowired
     private HtmlPdfService pdfService;
    
@KafkaListener(topics = "html-pdf-svc.processed", groupId = "group_id") 
  public void consume(ConsumerRecord<String, String> record) throws IOException 
{ 
 	logger.debug("Received message from topic: {}", record.topic());
     
    String[] parsedValues=pdfService.parseValues(record.value());
    Boolean isBlank=pdfService.readPdfUrl(parsedValues[1]);
    if(isBlank) {
   	 pdfService.processPdf(parsedValues[1],parsedValues[0],parsedValues[2]); 
    } 
    
  System.out.println("This is my message = " + record.value());
} 
	
	
}
