package com.project.hotelrest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class GuestController {

    private final GuestRepository guest_repo;
    private final BedroomRepository bedroom_repo;
    private final GuestResourceAssembler guest_assembler;
    private final HotelRepository hotel_repo;

    GuestController(GuestRepository guest_repo, BedroomRepository bedroom_repo, HotelRepository hotel_repo,GuestResourceAssembler guest_assembler){
        this.bedroom_repo = bedroom_repo;
        this.guest_repo = guest_repo;
        this.hotel_repo = hotel_repo;
        this.guest_assembler = guest_assembler;
    }

    // List Guests
   @GetMapping(value = "/hoteis/{id}/hospedes", produces = "application/json; charset=UTF-8")
    //public Resources<Resource<Guest>> all (@PathVariable Long id){
    public Resources<Resource<Guest>> allGuests (@PathVariable Long id){

        List<Resource<Guest>> guests_resource;
        List<Guest> guests;

        guests = guest_repo.findGuestsByBedroom_Hotel_Id(id);

        guests_resource = guests.stream()
                .map(guest_assembler::toResource)
                .collect(Collectors.toList());


       return new Resources<>(guests_resource,
               linkTo(methodOn(GuestController.class).allGuests(id)).withSelfRel());

    }

    @GetMapping(value = "/hoteis/{id_hotel}/hospedes/{id_guest}", produces = "application/json; charset=UTF-8")
    //public Resources<Resource<Guest>> all (@PathVariable Long id){
    public Resource<Guest> oneGuest (@PathVariable("id_hotel") Long id_hotel,
                                 @PathVariable("id_guest") Long id_guest){

        Guest guest = guest_repo.findById(id_guest).orElseThrow(() -> new GuestNotFoundException(id_guest));



        return guest_assembler.toResource(guest);

    }

    // Statistics
    @GetMapping( value = "/hospedes/estatistica", produces = "application/json; charset=UTF-8")
    public GuestStatistic guestsStatistic(@RequestParam(value = "location", defaultValue = "all") String location){

        List<Hotel> hotels;
        List<Bedroom> bedrooms = new ArrayList<Bedroom>();
        GuestStatistic guest_stats = new GuestStatistic();

        if(location.equals("all")){
            hotels = hotel_repo.findAll();
        }
        else{
            hotels = hotel_repo.findHotelsByState(location);
        }

        int other_count = 0;
        int male_count = 0;
        int female_count = 0;

        for(int i = 0; i < hotels.size(); i++) {
            other_count += guest_repo.countGuestByGenderAndBedroom_Hotel_Id("Other", hotels.get(i).getId());
            male_count += guest_repo.countGuestByGenderAndBedroom_Hotel_Id("Male", hotels.get(i).getId());
            female_count += guest_repo.countGuestByGenderAndBedroom_Hotel_Id("Female", hotels.get(i).getId());
        }

        guest_stats.setGender_female_count(female_count);
        guest_stats.setGender_male_count(male_count);
        guest_stats.setGender_other_count(other_count);

        return guest_stats;

    }


    // Create Guest
    @PostMapping(value = "/hoteis/{id}/hospedes", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> newGuest(@RequestBody Guest newGuest, @PathVariable Long id) throws URISyntaxException {
        Hotel hotel = hotel_repo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        Resource<Guest> resource = guest_assembler.toResource(guest_repo.save(newGuest));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }


}
