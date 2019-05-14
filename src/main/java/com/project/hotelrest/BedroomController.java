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
public class BedroomController {

    private final BedroomRepository bedroom_repo;
    private final HotelRepository hotel_repo;
    private final BedroomResourceAssembler bedroom_assembler;

    BedroomController(BedroomRepository bedroom_repo, HotelRepository hotel_repo, BedroomResourceAssembler bedroom_assembler){
        this.bedroom_repo = bedroom_repo;
        this.hotel_repo = hotel_repo;
        this.bedroom_assembler = bedroom_assembler;
    }

    // List Bedrooms
    @GetMapping(value = "/quartos", produces = "application/json; charset=UTF-8")
    public Resources<Resource<Bedroom>> allBedroomsRoot (@RequestParam(value="occupation", defaultValue="all") String occupation){

        List<Resource<Bedroom>> bedrooms_resource;
        List<Bedroom> bedrooms;

        if(occupation.equals("free")){
            bedrooms = bedroom_repo.findBedroomByOccupied(false);
        }
        else if(occupation.equals("occupied")){
            bedrooms = bedroom_repo.findBedroomByOccupied(true);
        }
        else{
            bedrooms = bedroom_repo.findAll();
        }

        bedrooms_resource = bedrooms.stream()
                .map(bedroom_assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bedrooms_resource,
                linkTo(methodOn(BedroomController.class).allBedroomsRoot(null)).withSelfRel());
    }

    // Select Bedroom
    @GetMapping(value = "/quartos/{id_bedroom}", produces = "application/json; charset=UTF-8")
    Resource<Bedroom> oneBedroomRoot (@PathVariable Long id_bedroom){

        Bedroom bedroom = bedroom_repo.findById(id_bedroom)
                .orElseThrow(() -> new BedroomNotFoundException(id_bedroom));

        return bedroom_assembler.toResource(bedroom);
    }

    // List Bedrooms
    @GetMapping(value = "/hoteis/{id}/quartos", produces = "application/json; charset=UTF-8")
    public Resources<Resource<Bedroom>> allBedrooms (@RequestParam(value="occupation", defaultValue="all") String occupation,
            @RequestParam(value="min_beds", defaultValue="0") int min_beds ,@PathVariable Long id){

        List<Resource<Bedroom>> bedrooms_resource;
        List<Bedroom> bedrooms;

        if(occupation.equals("free")){
            if(min_beds != 0){
                bedrooms = bedroom_repo.findByHotelIdOccupiedRooms(id,false,min_beds);
            }
            else{
                bedrooms = bedroom_repo.findBedroomsByHotel_IdAndOccupied(id,false);
            }
        }
        else if(occupation.equals("occupied")){
            bedrooms = bedroom_repo.findByHotelIdByOccupation(id,true);
        }
        else{
            bedrooms = bedroom_repo.findByHotelId(id);
        }

        bedrooms_resource = bedrooms.stream()
                .map(bedroom_assembler::toResource)
                .collect(Collectors.toList());

        return new Resources<>(bedrooms_resource,
                linkTo(methodOn(BedroomController.class).allBedrooms(occupation,min_beds,id)).withSelfRel());
    }

    // Select Bedroom
    @GetMapping(value = "/hoteis/{id_hotel}/quartos/{num_bedroom}", produces = "application/json; charset=UTF-8")
    Resource<Bedroom> oneBedroom (@PathVariable("id_hotel") Long id_hotel,
                                  @PathVariable("num_bedroom") int num_bedroom){

        Bedroom bedroom = bedroom_repo.findBedroomByNumberAndHotelId(id_hotel,num_bedroom);

        return bedroom_assembler.toResource(bedroom);
    }

    // Create Bedroom
    @PostMapping(value = "/hotel/{id}/quarto", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> newBedroom(@RequestBody Bedroom newBedroom, @PathVariable Long id) throws URISyntaxException {
        Hotel hotel = hotel_repo.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
        newBedroom.setHotel(hotel);
        Resource<Bedroom> resource = bedroom_assembler.toResource(bedroom_repo.save(newBedroom));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

    // Change Bedroom Data, most importantly its occupation
    @PutMapping(value = "/hoteis/{id_hotel}/quartos/{id_bedroom}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> changeBedroom(@RequestBody Bedroom newBedroom, @PathVariable("id_hotel") Long id_hotel,
                                    @PathVariable("id_bedroom") Long id_bedroom) throws URISyntaxException{

        Resource<Bedroom> resource = bedroom_assembler.toResource(bedroom_repo.findById(id_bedroom)
                .map(bedroom -> {
                    bedroom.setHotel(newBedroom.getHotel());
                    bedroom.setNum_beds(newBedroom.getNum_beds());
                    bedroom.setNumber(newBedroom.getNumber());
                    bedroom.setPrice(newBedroom.getPrice());

                    bedroom.setOccupied(newBedroom.getOccupied());
                    return bedroom_repo.save(bedroom);
                })
                .orElseGet(() -> {
                    newBedroom.setId(id_bedroom);
                    return bedroom_repo.save(newBedroom);
                }));

        return ResponseEntity
                .created(new URI(resource.getId().expand().getHref()))
                .body(resource);
    }

}
