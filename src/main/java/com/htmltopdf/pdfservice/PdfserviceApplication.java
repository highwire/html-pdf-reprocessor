package com.htmltopdf.pdfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
 public class PdfserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfserviceApplication.class, args);
	}

}
