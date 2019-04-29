package com.project.hotelrest;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Hotel {

    private @Id @GeneratedValue Long id;
    private String name;
    private int stars;
    private String state;

    public Hotel(){
        this.name = "Default Name";
        this.stars = 0;
        this.state = "NONE";
    }
    public Hotel(String name, int stars, String state){
        this.name = name;
        this.stars = stars;
        this.state = state;
    }
}
