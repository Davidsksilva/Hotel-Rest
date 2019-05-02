package com.project.hotelrest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class GuestResourceAssembler implements ResourceAssembler<Guest,Resource<Guest>> {
    @Override
    public Resource<Guest> toResource(Guest guest){
        return new Resource<>(guest,
                linkTo(methodOn(GuestController.class).oneGuest (guest.getBedroom().getHotel().getId(),guest.getId())).withSelfRel(),
                linkTo(methodOn(GuestController.class).allGuests(guest.getBedroom().getHotel().getId())).withRel("guests"));
    }
}
