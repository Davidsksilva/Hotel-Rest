package com.project.hotelrest;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity

public class Bedroom {

    private @Id @GeneratedValue Long id;
    private int number;
    private int numBeds;
    private float price;
    private boolean occupied;

    @ManyToOne
    private Hotel hotel;

    public Bedroom(){

    }

    public Bedroom(int number,int numBeds, float price, boolean occupied, Hotel hotel){
        this.number = number;
        this.numBeds = numBeds;
        this.price = price;
        this.occupied = occupied;
        this.hotel = hotel;
    }

    public boolean getOccupied(){
        return this.occupied;
    }
}
