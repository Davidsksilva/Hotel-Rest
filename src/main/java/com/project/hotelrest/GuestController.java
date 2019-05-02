package com.project.hotelrest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
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
   @GetMapping(value = "/hotels/{id}/guests", produces = "application/json; charset=UTF-8")
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

    @GetMapping(value = "/hotels/{id_hotel}/guests/{id_guest}", produces = "application/json; charset=UTF-8")
    //public Resources<Resource<Guest>> all (@PathVariable Long id){
    public Resource<Guest> oneGuest (@PathVariable("id_hotel") Long id_hotel,
                                 @PathVariable("id_guest") Long id_guest){

        Guest guest = guest_repo.findById(id_guest).orElseThrow(() -> new GuestNotFoundException(id_guest));



        return guest_assembler.toResource(guest);

    }

    // Create Guest
    @PostMapping(value = "/hotels/{id}/guests", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> newGuest(@RequestBody Guest newGuest, @PathVariable Long id) throws URISyntaxException {
        Hotel hotel = hotel_repo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        Resource<Guest> resource = guest_assembler.toResource(guest_repo.save(newGuest));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }


}
