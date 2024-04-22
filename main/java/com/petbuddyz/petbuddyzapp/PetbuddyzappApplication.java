package com.petbuddyz.petbuddyzapp;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;

@SpringBootApplication
public class PetbuddyzappApplication {

	public static void main(String[] args) throws URISyntaxException {
		SpringApplication.run(PetbuddyzappApplication.class, args);

		// Map<String, Object> paramaters = new HashMap<>();
		// paramaters.put("name", "John Doe");

		// Traverson traverson = new Traverson(new URI("http://localhost:8080/owners"), MediaTypes.HAL_JSON);
		// String owner = traverson.follow("self").toObject("$._embedded.owners[0].ownerName");
		// System.out.println(owner);
	}

}
