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

    private final GuestRepository guestRepository;
    private final GuestResourceAssembler guestResourceAssembler;
    private final HotelRepository hotelRepository;

    GuestController(GuestRepository guestRepository, HotelRepository hotelRepository, GuestResourceAssembler guestResourceAssembler){
        this.guestRepository = guestRepository;
        this.hotelRepository = hotelRepository;
        this.guestResourceAssembler = guestResourceAssembler;
    }

    /**
     * List Guests
     * @param id
     * @return
     */
    @GetMapping(value = "/hoteis/{id}/hospedes", produces = "application/json; charset=UTF-8")
    public Resources<Resource<Guest>> allGuests (@PathVariable Long id){

        List<Resource<Guest>> guests_resource;
        List<Guest> guests;

        guests = guestRepository.findGuestsByBedroom_Hotel_Id(id);

        guests_resource = guests.stream()
                .map(guestResourceAssembler::toResource)
                .collect(Collectors.toList());


       return new Resources<>(guests_resource,
               linkTo(methodOn(GuestController.class).allGuests(id)).withSelfRel());

    }

    /**
     *
     * @param id_hotel
     * @param id_guest
     * @return
     */
    @GetMapping(value = "/hoteis/{id_hotel}/hospedes/{id_guest}", produces = "application/json; charset=UTF-8")
    public Resource<Guest> oneGuest (@PathVariable("id_hotel") Long id_hotel,
                                 @PathVariable("id_guest") Long id_guest){

        Guest guest = guestRepository.findById(id_guest).orElseThrow(() -> new GuestNotFoundException(id_guest));



        return guestResourceAssembler.toResource(guest);

    }

    /**
     * Statistics
     * @param location
     * @return
     */
    @GetMapping( value = "/hospedes/estatistica", produces = "application/json; charset=UTF-8")
    public GuestStatistic guestsStatistic(@RequestParam(value = "location", defaultValue = "all") String location){

        List<Hotel> hotels;
        List<Bedroom> bedrooms = new ArrayList<Bedroom>();
        GuestStatistic guest_stats = new GuestStatistic();

        if(location.equals("all")){
            hotels = hotelRepository.findAll();
        }
        else{
            hotels = hotelRepository.findHotelsByState(location);
        }

        int other_count = 0;
        int male_count = 0;
        int female_count = 0;

        for(int i = 0; i < hotels.size(); i++) {
            other_count += guestRepository.countGuestByGenderAndBedroom_Hotel_Id("Other", hotels.get(i).getId());
            male_count += guestRepository.countGuestByGenderAndBedroom_Hotel_Id("Male", hotels.get(i).getId());
            female_count += guestRepository.countGuestByGenderAndBedroom_Hotel_Id("Female", hotels.get(i).getId());
        }

        guest_stats.setGender_female_count(female_count);
        guest_stats.setGender_male_count(male_count);
        guest_stats.setGender_other_count(other_count);

        return guest_stats;

    }


    /**
     *
     * @param newGuest
     * @param id
     * @return
     * @throws URISyntaxException
     */
    @PostMapping(value = "/hoteis/{id}/hospedes", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> newGuest(@RequestBody Guest newGuest, @PathVariable Long id) throws URISyntaxException {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        Resource<Guest> resource = guestResourceAssembler.toResource(guestRepository.save(newGuest));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }


}
