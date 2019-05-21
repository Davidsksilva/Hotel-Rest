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
    private final BedroomRepository bedroom_repo;

    private final HotelResourceAssembler hotel_assembler;


    HotelController(HotelRepository hotel_repo,BedroomRepository bedroom_repo, HotelResourceAssembler hotel_assembler){
        this.hotel_repo = hotel_repo;
        this.bedroom_repo = bedroom_repo;
        this.hotel_assembler = hotel_assembler;
    }

    // List Hotels
    @GetMapping(value = "/hoteis", produces = "application/json; charset=UTF-8")
    Resources<Resource<Hotel>> all(@RequestParam(value = "location", defaultValue = "all") String location) {

        if(location.equals("all")){
            List<Resource<Hotel>> hotels = hotel_repo.findAll().stream()
                    .map(hotel_assembler::toResource)
                    .collect(Collectors.toList());
            return new Resources<>(hotels,
                    linkTo(methodOn(HotelController.class).all(location)).withSelfRel());
        }
        else{
            List<Resource<Hotel>> hotels = hotel_repo.findHotelsByState(location).stream()
                    .map(hotel_assembler::toResource)
                    .collect(Collectors.toList());
            return new Resources<>(hotels,
                    linkTo(methodOn(HotelController.class).all(location)).withSelfRel());

        }
    }

    // Select Hotel
    @GetMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
    public Resource<Hotel> one (@PathVariable Long id){
        Hotel hotel = hotel_repo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return hotel_assembler.toResource(hotel);
    }

    // Statistics
    @GetMapping(value = "/hoteis/estatistica", produces = "application/json; charset=UTF-8")
    public HotelStatistic allHotelsStatistics (@RequestParam(value = "location", defaultValue = "all") String location){

        List<Hotel> hotels;
        HotelStatistic hotel_stats = new HotelStatistic();

        if(location.equals("all")){
           hotels = hotel_repo.findAll();
        }
        else{
            hotels = hotel_repo.findHotelsByState(location);
        }
        hotel_stats.setHotel_count(hotels.size());
        hotel_stats.setLocation(location);

        int occupiedBedroomCount = 0;
        int bedroomCount = 0;
        for(int i = 0; i < hotels.size(); i++){

            occupiedBedroomCount += bedroom_repo.countOccupiedBedroomsInHotel(hotels.get(i));
            bedroomCount += bedroom_repo.countBedroomsInHotel(hotels.get(i));
        }

        hotel_stats.setBedroom_count(bedroomCount);
        hotel_stats.setOccupied_bedroom_count(occupiedBedroomCount);
        hotel_stats.setOccupation((occupiedBedroomCount/bedroomCount)*100);

        return hotel_stats;

    }

    // Create Hotel
    @PostMapping("/hoteis")
    ResponseEntity<?> newHotel(@RequestBody Hotel newHotel) throws URISyntaxException {

        Resource<Hotel> resource = hotel_assembler.toResource(hotel_repo.save(newHotel));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Change Hotel data
    @PutMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
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
    @DeleteMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> deleteHotel(@PathVariable Long id){
        hotel_repo.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
