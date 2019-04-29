package com.project.hotelrest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HotelController {

    private final HotelRepository repository;

    HotelController(HotelRepository repository){
        this.repository = repository;
    }

    // Aggregate root

    @GetMapping("/hotels")
    List<Hotel> all(){
        return repository.findAll();
    }

    @PostMapping("/hotels")
    Hotel newHotel(@RequestBody Hotel newHotel){
        return repository.save(newHotel);
    }

    // Single item

    @GetMapping("/hotels/{id}")
    Hotel one (@PathVariable Long id){
        return repository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(id));
    }

    @PutMapping("/hotels/{id}")
    Hotel replaceHotel(@RequestBody Hotel newHotel, @PathVariable Long id){
        return repository.findById(id)
                .map(hotel -> {
                    hotel.setName(newHotel.getName());
                    hotel.setStars(newHotel.getStars());
                    hotel.setState(newHotel.getState());
                    return repository.save(hotel);
                })
                .orElseGet(() -> {
                    newHotel.setId(id);
                    return repository.save(newHotel);
                });
    }

    @DeleteMapping("/hotels/{id}")
    void deleteHotel(@PathVariable Long id){
        repository.deleteById(id);
    }
}
