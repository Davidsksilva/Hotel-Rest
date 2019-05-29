package com.project.hotelrest;

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

    private final HotelRepository hotelRepository;
    private final BedroomRepository bedroomRepository;

    private final HotelResourceAssembler hotel_assembler;

    /**
     *
     * @param hotelRepository
     * @param bedroomRepository
     * @param hotel_assembler
     */
    HotelController(HotelRepository hotelRepository, BedroomRepository bedroomRepository, HotelResourceAssembler hotel_assembler){
        this.hotelRepository = hotelRepository;
        this.bedroomRepository = bedroomRepository;
        this.hotel_assembler = hotel_assembler;
    }

    /**
     * List Hotels
     * @param location
     * @return
     */
    @GetMapping(value = "/hoteis", produces = "application/json; charset=UTF-8")
    Resources<Resource<Hotel>> all(@RequestParam(value = "location", defaultValue = "all") String location) {

        if(location.equals("all")){
            List<Resource<Hotel>> hotels = hotelRepository.findAll().stream()
                    .map(hotel_assembler::toResource)
                    .collect(Collectors.toList());
            return new Resources<>(hotels,
                    linkTo(methodOn(HotelController.class).all(location)).withSelfRel());
        }
        else{
            List<Resource<Hotel>> hotels = hotelRepository.findHotelsByState(location).stream()
                    .map(hotel_assembler::toResource)
                    .collect(Collectors.toList());
            return new Resources<>(hotels,
                    linkTo(methodOn(HotelController.class).all(location)).withSelfRel());

        }
    }

    /**
     * Select Hotel
     * @param id
     * @return
     */
    @GetMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
    public Resource<Hotel> one (@PathVariable Long id){
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));

        return hotel_assembler.toResource(hotel);
    }

    /**
     * Statistics
     * @param location
     * @return
     */
    @GetMapping(value = "/hoteis/estatistica", produces = "application/json; charset=UTF-8")
    public HotelStatistic allHotelsStatistics (@RequestParam(value = "location", defaultValue = "all") String location){

        List<Hotel> hotels;
        HotelStatistic hotel_stats = new HotelStatistic();
        float avgPrice;
        float revenue;

        if(location.equals("all")){
           hotels = hotelRepository.findAll();
           avgPrice = bedroomRepository.avgPrice();
           revenue = bedroomRepository.sumSold();
        }
        else{
            hotels = hotelRepository.findHotelsByState(location);
            avgPrice = bedroomRepository.avgPriceByLocation(location);
            revenue = bedroomRepository.sumSoldByLocation(location);
        }
        hotel_stats.setHotel_count(hotels.size());
        hotel_stats.setLocation(location);

        int occupiedBedroomCount = 0;
        int bedroomCount = 0;
        for(int i = 0; i < hotels.size(); i++){

            occupiedBedroomCount += bedroomRepository.countOccupiedBedroomsInHotel(hotels.get(i));
            bedroomCount += bedroomRepository.countBedroomsInHotel(hotels.get(i));
        }



        hotel_stats.setAverage_price(avgPrice);
        hotel_stats.setRevenue(revenue);
        hotel_stats.setBedroom_count(bedroomCount);
        hotel_stats.setOccupied_bedroom_count(occupiedBedroomCount);
        float occupation = ((float)occupiedBedroomCount/(float)bedroomCount);
        hotel_stats.setOccupation(occupation*100);

        return hotel_stats;

    }

    /**
     * Create Hotel
     * @param newHotel
     * @return
     * @throws URISyntaxException
     */
    @PostMapping("/hoteis")
    ResponseEntity<?> newHotel(@RequestBody Hotel newHotel) throws URISyntaxException {

        Resource<Hotel> resource = hotel_assembler.toResource(hotelRepository.save(newHotel));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    /**
     * Change Hotel data
     * @param newHotel
     * @param id
     * @return
     */
    @PutMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
    Hotel replaceHotel(@RequestBody Hotel newHotel, @PathVariable Long id){
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setName(newHotel.getName());
                    hotel.setStars(newHotel.getStars());
                    hotel.setState(newHotel.getState());
                    return hotelRepository.save(hotel);
                })
                .orElseGet(() -> {
                    newHotel.setId(id);
                    return hotelRepository.save(newHotel);
                });
    }

    /**
     * Change Hotel data
     * @param id
     * @return
     */
    @DeleteMapping(value = "/hoteis/{id}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> deleteHotel(@PathVariable Long id){
        hotelRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

}
