package com.project.hotelrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class HotelController {

    private final HotelRepository hotel_repo;
    private final HotelResourceAssembler hotel_assembler;


    HotelController(HotelRepository hotel_repo, HotelResourceAssembler hotel_assembler){
        this.hotel_repo = hotel_repo;
        this.hotel_assembler = hotel_assembler;
    }

    // List Hotels
    @GetMapping(value = "/hotels", produces = "application/json; charset=UTF-8")
    Resources<Resource<Hotel>> all() {

        List<Resource<Hotel>> hotels = hotel_repo.findAll().stream()
                .map(hotel_assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(hotels,
                linkTo(methodOn(HotelController.class).all()).withSelfRel());
    }

    // Select Hotel
    @GetMapping(value = "/hotels/{id}", produces = "application/json; charset=UTF-8")
    public Resource<Hotel> one (@PathVariable Long id){
        Hotel hotel = hotel_repo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return hotel_assembler.toResource(hotel);
    }

    // Create Hotel
    @PostMapping("/hotels")
    ResponseEntity<?> newHotel(@RequestBody Hotel newHotel) throws URISyntaxException {

        Resource<Hotel> resource = hotel_assembler.toResource(hotel_repo.save(newHotel));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Change Hotel data
    @PutMapping(value = "/hotels/{id}", produces = "application/json; charset=UTF-8")
    Hotel replaceHotel(@RequestBody Hotel newHotel, @PathVariable Long id){
        return hotel_repo.findById(id)
                .map(hotel -> {
                    hotel.setName(newHotel.getName());
                    hotel.setStars(newHotel.getStars());
                    hotel.setState(newHotel.getState());
                    return hotel_repo.save(hotel);
                })
                .orElseGet(() -> {
                    newHotel.setId(id);
                    return hotel_repo.save(newHotel);
                });
    }

    // Delete Hotel by id
    @DeleteMapping(value = "/hotels/{id}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> deleteHotel(@PathVariable Long id){
        hotel_repo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
