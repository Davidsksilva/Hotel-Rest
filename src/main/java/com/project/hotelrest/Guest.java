package com.project.hotelrest;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Guest {

    private @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)  Long id;
    private String name;
    private int age;
    private String sex;

    public Guest(){
        this.name = "John Doe";
        this.age = 10;
        this.sex = "Male";
    }

    public Guest(String name, int age, String sex){
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

}
