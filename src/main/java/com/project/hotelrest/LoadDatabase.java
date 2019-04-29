package com.project.hotelrest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(HotelRepository repository){
        return args ->{

            //log.info("Preloading " + repository.save(new Hotel ("Hotel Cabo Branco", 5, "PB")));
        };
    }
}
