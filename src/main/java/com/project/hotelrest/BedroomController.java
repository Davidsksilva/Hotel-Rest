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
    private final GuestRepository guest_repo;

    private final BedroomResourceAssembler bedroom_assembler;
    private final GuestResourceAssembler guest_assembler;

    /**
     * Constructor function.
     * @param bedroom_repo the Bedroom entity Repository.
     * @param hotel_repo the Hotel entity Repository.
     * @param guest_repo the Guest entity Repository.
     * @param bedroom_assembler the assembler to transform the Bedroom entity into a Resource.
     * @param guest_assembler the assembler to transform the Guest entity into a Resource.
     */
    BedroomController(BedroomRepository bedroom_repo, HotelRepository hotel_repo, GuestRepository guest_repo,
                      BedroomResourceAssembler bedroom_assembler, GuestResourceAssembler guest_assembler){
        this.bedroom_repo = bedroom_repo;
        this.hotel_repo = hotel_repo;
        this.guest_repo = guest_repo;
        this.bedroom_assembler = bedroom_assembler;
        this.guest_assembler= guest_assembler;
    }

    /**
     * Endpoint to list all bedrooms on the Repository, filtered by occupation. Not using the hotel path.
     * @param occupation String, "all" lists all bedrooms, "occupied" only occupied bedrooms and "free" only available bedrooms.
     * @return Resource containing a list of Bedroom resources, with corresponding hyperlinks.
     */
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

    /**
     * Endpoint to get a single Bedroom by its id on the Repository. Not using the hotel path.
     * @param id_bedroom id of the hotel in the Repository.
     * @return Resource containing the referred Bedroom.
     */
    @GetMapping(value = "/quartos/{id_bedroom}", produces = "application/json; charset=UTF-8")
    Resource<Bedroom> oneBedroomRoot (@PathVariable Long id_bedroom){

        Bedroom bedroom = bedroom_repo.findById(id_bedroom)
                .orElseThrow(() -> new BedroomNotFoundException(id_bedroom));

        return bedroom_assembler.toResource(bedroom);
    }


    /**
     * Endpoint to list all bedrooms in a Hotel.
     * @param occupation String, "all" lists all bedrooms, "occupied" only occupied bedrooms and "free" only available bedrooms.
     * @param minBeds Integer, specifying the minimum number of beds that a Bedroom needs to have to be listed.
     * @param id Long, the id of the Hotel.
     * @return Resource containing a list of Bedroom resources, with corresponding hyperlinks.
     */
    @GetMapping(value = "/hoteis/{id}/quartos", produces = "application/json; charset=UTF-8")
    public Resources<Resource<Bedroom>> allBedrooms (@RequestParam(value="occupation", defaultValue="all") String occupation,
            @RequestParam(value="min_beds", defaultValue="0") int minBeds ,@PathVariable Long id){

        List<Resource<Bedroom>> bedrooms_resource;
        List<Bedroom> bedrooms;

        if(occupation.equals("free")){
            if(minBeds != 0){
                bedrooms = bedroom_repo.findByHotelIdOccupiedRooms(id,false,minBeds);
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
                linkTo(methodOn(BedroomController.class).allBedrooms(occupation,minBeds,id)).withSelfRel());
    }

    /**
     *
     * @param id_hotel
     * @param num_bedroom
     * @return
     */
    @GetMapping(value = "/hoteis/{id_hotel}/quartos/{num_bedroom}", produces = "application/json; charset=UTF-8")
    Resource<Bedroom> oneBedroom (@PathVariable("id_hotel") Long id_hotel,
                                  @PathVariable("num_bedroom") int num_bedroom){

        Bedroom bedroom = bedroom_repo.findBedroomByNumberAndHotelId(id_hotel,num_bedroom);

        return bedroom_assembler.toResource(bedroom);
    }

    /**
     *
     * @param newBedroom
     * @param id
     * @return
     * @throws URISyntaxException
     */
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

    /**
     * Endpoint to insert a Guest on a Bedroom.
     * @param newGuest
     * @param id_hotel
     * @param num_bedroom
     * @return
     * @throws URISyntaxException
     */
    @PostMapping(value = "/hotel/{id_hotel}/quartos/{num_bedroom}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> newGuestOnBedroom(@RequestBody Guest newGuest,
                                        @PathVariable("id_hotel") Long id_hotel,
                                        @PathVariable("num_bedroom") int num_bedroom) throws URISyntaxException {
        Bedroom bedroom = bedroom_repo.findBedroomByHotel_IdAndNumber(id_hotel,num_bedroom);

        if(!bedroom.getOccupied()){

            newGuest.setBedroom(bedroom);
            bedroom.setOccupied(true);
            bedroom_repo.save(bedroom);
            Resource resource = guest_assembler.toResource(guest_repo.save(newGuest));
            return ResponseEntity
                    .created(new URI(resource.getId().expand().getHref()))
                    .body(resource);

        }
        else{
            return ResponseEntity.created(new URI("/hotel/" + id_hotel + "/quarto/" + num_bedroom))
                    .body(null);
        }


    }

    /**
     * Change Bedroom Data, most importantly its occupation.
     * @param newBedroom
     * @param id_hotel
     * @param id_bedroom
     * @return
     * @throws URISyntaxException
     */
    @PutMapping(value = "/hoteis/{id_hotel}/quartos/{id_bedroom}", produces = "application/json; charset=UTF-8")
    ResponseEntity<?> changeBedroom(@RequestBody Bedroom newBedroom, @PathVariable("id_hotel") Long id_hotel,
                                    @PathVariable("id_bedroom") Long id_bedroom) throws URISyntaxException{

        Resource<Bedroom> resource = bedroom_assembler.toResource(bedroom_repo.findById(id_bedroom)
                .map(bedroom -> {
                    bedroom.setHotel(newBedroom.getHotel());
                    bedroom.setNumBeds(newBedroom.getNumBeds());
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
