package com.project.hotelrest;

import lombok.Data;
import javax.persistence.GenerationType;
import javax.persistence.*;

@Data
@Entity
public class Bedroom {

    private @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)  Long id;
    private int number;
    private int num_beds;
    private float price;
    private boolean occupied;

    @ManyToOne
    private Hotel hotel;
    @ManyToOne
    private Guest guest;

    public Bedroom(){
        num_beds = 1;
        price = 100;
        occupied = false;
    }

    public Bedroom(int num_beds, float price, boolean occupied){
        this.num_beds = num_beds;
        this.price = price;
        this.occupied = occupied;
    }

    public Bedroom(int number,int num_beds, float price, boolean occupied, Hotel hotel, Guest guest){
        this.number = number;
        this.num_beds = num_beds;
        this.price = price;
        this.occupied = occupied;
        this.hotel = hotel;
        this.guest = guest;
    }

    public Bedroom(int number,int num_beds, float price, boolean occupied, Hotel hotel){
        this.number = number;
        this.num_beds = num_beds;
        this.price = price;
        this.occupied = occupied;
        this.hotel = hotel;
    }

    public boolean getOccupied(){
        return this.occupied;
    }
}
