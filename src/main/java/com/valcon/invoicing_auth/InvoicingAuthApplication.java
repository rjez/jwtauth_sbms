package com.valcon.invoicing_auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.valcon.invoicing_auth.controller.SampleDataService;

@SpringBootApplication(exclude = { ErrorMvcAutoConfiguration.class })
public class InvoicingAuthApplication {
	
	final static Logger LOG = LoggerFactory.getLogger(InvoicingAuthApplication.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(InvoicingAuthApplication.class, args);
		SampleDataService dataSetup = ctx.getBean(SampleDataService.class);
		dataSetup.createSampleData();
		LOG.info("Invoicing auth application started.");
	}

}