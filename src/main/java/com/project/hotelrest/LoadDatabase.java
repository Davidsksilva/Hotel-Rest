package com.project.hotelrest;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
@Slf4j
public class LoadDatabase {

    private static int getRandomIntegerBetweenRange(int min, int max){
        return (int)(Math.random()*((max-min)+1))+min;
    }

    private boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }

    // Populate Database
    //@Bean
    CommandLineRunner initDatabase(HotelRepository h_repository, BedroomRepository br_repository, GuestRepository g_repository){
        return args ->{

            // Variables
            List<String> uf_list = Arrays.asList("AC", "AL", "AP","AM","BA","CE",
                    "DF","ES","GO","MA","MT","MS","MG","PA","PB","PR","PE","PI","RR","RO",
                    "RJ","RN","RS","SC","SP","SE","TO");

            List<String> sex_list = Arrays.asList("Male", "Female", "Other");

            Faker faker = new Faker(new Locale("pt"));

            // Hotel Loop
            for(int i=0; i<30; i++){

                // Random UF index
                int uf_index = getRandomIntegerBetweenRange(0,25);

                // Generating Random entry values for Hotel entity
                int stars = getRandomIntegerBetweenRange(1,5);
                String uf = uf_list.get(uf_index);
                String name = faker.company().name();


               // log.info("Preloading " + h_repository.save(hotel));

                //Generating Random entry values for Bedroom entities
                int num_bedrooms = getRandomIntegerBetweenRange(1,20);

                // Create Hotel entity
                Hotel hotel = new Hotel (name, stars, uf);
                h_repository.save(hotel);

                // Bedroom Loop
                for(int j = 1; j <= num_bedrooms; j++){
                    boolean occupied = getRandomBoolean();
                    int num_beds = getRandomIntegerBetweenRange(1,4);
                    int price = getRandomIntegerBetweenRange(10,50) * 10;

                    if(occupied){

                        // Generating Random entry values for Guest entity

                        String guest_name = faker.name().fullName();
                        int age = getRandomIntegerBetweenRange(20,70);
                        int sex_index = getRandomIntegerBetweenRange(0,2);
                        String sex = sex_list.get(sex_index);
                        Bedroom bedroom = new Bedroom(j,num_beds, price,true,hotel);

                        Guest guest = new Guest (guest_name, age, sex,bedroom);

                        //long id_g = g_repository.save(new Guest (guest_name, age, sex)).getId();
                        //Guest guest = g_repository.findById(id_g).orElseThrow(() -> new GuestNotFoundException(id_g));
                        br_repository.save(bedroom);
                        g_repository.save(guest);


                        //long id_b = br_repository.save(new Bedroom(j,num_beds, price,true,hotel)).getId();
                       // Bedroom bedroom = br_repository.findById(id_b).orElseThrow(() -> new BedroomNotFoundException(id_b));

                       // guest.assignBedroom(bedroom);
                        //br_repository.save(bedroom);
                        //g_repository.save(guest);

                       // log.info("Preloading " + id = );


                        //log.info("Preloading " + br_repository.save(bedroom));

                        //g_repository.findById()
                        //guest.assignBedroom(bedroom);
                    }
                    else{
                        br_repository.save(new Bedroom(j,num_beds, price,false,hotel));
                        //log.info("Preloading " + br_repository.save(new Bedroom(j,num_beds, price,false,hotel,null)));
                    }
                }
            }
        };
    }
}
