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
    private String sex;

    //@OneToOne(cascade=CascadeType.ALL, mappedBy="guest")
    @ManyToOne
    private Bedroom bedroom;

    public Guest(){
        this.name = "John Doe";
        this.age = 10;
        this.sex = "Male";
    }

    public Guest(String name, int age, String sex, Bedroom bedroom){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.bedroom = bedroom;
    }



}
