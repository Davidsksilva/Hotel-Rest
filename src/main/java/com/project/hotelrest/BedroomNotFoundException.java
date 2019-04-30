package com.project.hotelrest;

public class BedroomNotFoundException extends RuntimeException{

    BedroomNotFoundException(Long id){
        super("Could not find bedroom" + id);
    }
}
