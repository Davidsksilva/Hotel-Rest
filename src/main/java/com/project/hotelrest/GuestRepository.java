package com.project.hotelrest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findGuestsByBedroom_Hotel_Id(Long id);
}
