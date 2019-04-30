package com.project.hotelrest;

import org.springframework.data.jpa.repository.JpaRepository;

interface GuestRepository extends JpaRepository<Guest, Long> {
}
