package com.beac.estage_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Import;

// Cette annotation est plus puissante et plus explicite que 'exclude'
@SpringBootApplication
@Import({MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class EstageBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EstageBackendApplication.class, args);
	}

	// Nous n'avons PAS besoin du Bean pour SSLContext si Java 17 est utilisé.
	// Retirez-le pour simplifier.
}