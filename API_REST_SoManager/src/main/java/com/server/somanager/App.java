package com.server.somanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

import com.server.somanager.App;

@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@SpringBootApplication
public class App {


	//Si impossible de charger la classe : 
	// Maven/UpdateProject puis Project/clean
	//Run as configurations 
	
    public static void main(String[] args) {
    	try {
        	SpringApplication.run(App.class, args);
        	System.out.println("Application démarrée");
        	
        } catch (Exception e) {
        	System.out.println("Erreur application !");
        }
    }
}