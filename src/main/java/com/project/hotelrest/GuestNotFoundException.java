package com.project.hotelrest;

public class GuestNotFoundException extends RuntimeException {

    GuestNotFoundException(Long id){
        super("Could not find guest " + id);
    }
}
