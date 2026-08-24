package com.aadiandjava.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mindSpaceOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        Contact contact = new Contact()
                .name("MindSpace Support Team")
                .email("support@mindspace.campus.edu");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("MindSpace Enterprise — Mental Health & Emergency API")
                .version("1.0.0")
                .contact(contact)
                .description("Comprehensive REST API endpoints for Student Wellness, AI Assistance, " +
                             "Campus Counselors, Psychiatric Prescriptions, and Async SOS Emergency Dispatch.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}