/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.ecm.sigap.config;

import java.util.ResourceBundle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * @author Alfredo Morales
 * @version 1.0
 *
 */
@Configuration("SwaggerConfig")
@EnableSwagger2
public class SwaggerConfig {

	/** datos del servicio rest */
	private static ResourceBundle info = ResourceBundle.getBundle("app_info_swagger");

	/**
	 * Configuracionde Swagger.
	 * 
	 * @return
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)//
				.select()//

				.apis(RequestHandlerSelectors.any())//
				.paths(PathSelectors.any())//

				.build()//

				.apiInfo(apiInfo());
	}

	/**
	 * Informcacion de la applicacion.
	 * 
	 * @return
	 */
	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo(//
				info.getString("title"), //
				info.getString("description"), //
				info.getString("version"), //
				info.getString("termsOfServiceUrl"), new Contact(//
						info.getString("contact.name"), //
						info.getString("contact.url"), //
						info.getString("contact.email")), //
				info.getString("license"), //
				info.getString("licenseUrl"));
		return apiInfo;
	}
}