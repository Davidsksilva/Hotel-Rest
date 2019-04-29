package com.project.hotelrest;


import org.springframework.data.jpa.repository.JpaRepository;

interface HotelRepository extends JpaRepository<Hotel, Long> {
}
