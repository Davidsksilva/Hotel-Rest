package com.project.hotelrest;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Guest {

    private @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)  Long id;
    private String name;
    private int age;
    private String gender;

    @ManyToOne
    private Bedroom bedroom;

    public Guest(){

    }

    public Guest(String name, int age, String gender, Bedroom bedroom){
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.bedroom = bedroom;
    }



}
