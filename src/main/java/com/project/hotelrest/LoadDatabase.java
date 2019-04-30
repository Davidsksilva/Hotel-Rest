package com.project.hotelrest;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Configuration
@Slf4j
public class LoadDatabase {

    private static int getRandomIntegerBetweenRange(int min, int max){
        int x = (int)(Math.random()*((max-min)+1))+min;
        return x;
    }

    // Populate Database
    @Bean
    CommandLineRunner initDatabase(HotelRepository h_repository, BedroomRepository br_repository, GuestRepository g_repository){
        return args ->{

            // Variables
            List<String> uf_list = Arrays.asList("AC", "AL", "AP","AM","BA","CE",
                    "DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RR","RO",
                    "RJ","RN","RS","SC","SP","SE","TO");



            Faker faker = new Faker(new Locale("pt-BR"));

            for(int i=0; i<30; i++){

                int uf_index = getRandomIntegerBetweenRange(0,25);
                int stars = getRandomIntegerBetweenRange(1,5);
                String uf = uf_list.get(uf_index);

                log.info("Preloading " + h_repository.save(new Hotel ("Hotel Cabo Branco", stars, uf)));
            }


            // Generating fake Hotels



            log.info("Preloading " + h_repository.save(new Hotel ("Hotel Cabo Branco", 5, "PB")));
            Guest guest = new Guest (faker.name().fullName(), 21, "Male");
            log.info("Preloading " + g_repository.save(guest));
            List<Hotel> hotels = h_repository.findAll();
            for( Hotel hotel : hotels){
                log.info("Preloading " + br_repository.save(new Bedroom(1,1, 100,true,hotel,guest)));
                log.info("Preloading " + br_repository.save(new Bedroom(2,2, 200,true,hotel,guest)));

            }
            //log.info("Preloading " + repository.save(new Hotel ("Hotel Cabo Branco", 5, "PB")));
        };
    }
}
