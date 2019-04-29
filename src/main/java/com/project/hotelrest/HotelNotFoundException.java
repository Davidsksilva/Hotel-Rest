package com.project.hotelrest;

public class HotelNotFoundException extends RuntimeException {

    HotelNotFoundException(Long id){
        super("Could not find hotel" + id);
    }
}
