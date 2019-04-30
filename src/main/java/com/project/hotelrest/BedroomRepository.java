package com.project.hotelrest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BedroomRepository extends JpaRepository<Bedroom, Long> {
    @Query("select b from Bedroom b where b.hotel.id = ?1")
    List<Bedroom> findByHotelId(Long id);

    @Query("select b from Bedroom b where b.hotel.id = ?1 and b.occupied = ?2")
    List<Bedroom> findByHotelIdByOccupation(Long id, boolean occupied);

    List<Bedroom> findBedroomByOccupied(boolean occupied);

    @Query("select b from Bedroom b where b.hotel.id = ?1 and b.number= ?2")
    Bedroom findBedroomByNumberAndHotelId(Long id_hotel, int number);
}
