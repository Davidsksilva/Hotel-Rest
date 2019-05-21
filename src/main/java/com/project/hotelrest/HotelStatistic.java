package com.project.hotelrest;

import lombok.Data;

@Data
public class HotelStatistic {

    private int hotel_count;
    private String location;
    private float occupation;
    private int bedroom_count;
    private int occupied_bedroom_count;


    HotelStatistic(){

    }
}
