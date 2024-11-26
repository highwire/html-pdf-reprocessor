package org.highwire.htmlpdf.reprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
 public class HtmlPdfApplication {

	public static void main(String[] args) {
		SpringApplication.run(HtmlPdfApplication.class, args);
	}

}
