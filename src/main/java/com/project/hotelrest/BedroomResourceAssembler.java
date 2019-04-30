package com.project.hotelrest;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BedroomResourceAssembler implements ResourceAssembler<Bedroom, Resource<Bedroom>> {
    @Override
    public Resource<Bedroom> toResource(Bedroom bedroom){

        return new Resource<>(bedroom,
                linkTo(methodOn(BedroomController.class).oneBedroom (bedroom.getHotel().getId(),bedroom.getNumber())).withSelfRel(),
                linkTo(methodOn(BedroomController.class).allBedrooms(null,bedroom.getHotel().getId())).withRel("bedrooms"));
    }


}
