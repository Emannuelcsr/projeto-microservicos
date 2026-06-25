package br.com.projetosecsr.pedidos.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocOpenApiConfig {


	@Bean
	public OpenAPI openApi() {

	    License license = new License();

	    license.setName("Apache 2.0");
	    license.setUrl("https://www.apache.org/licenses/LICENSE-2.0");


	    Contact contact = new Contact();

	    contact.setName("Emannuel");
	    contact.setEmail("emannuelsouza@hotmail.com");


	    Info info = new Info();

	    info.setTitle("Rest Api - Pedidos");
	    info.setDescription("Api para realização de pedidos");
	    info.setLicense(license);
	    info.setContact(contact);


	    OpenAPI openAPI = new OpenAPI();

	    openAPI.setInfo(info);


	    return openAPI;
	}
	
}

