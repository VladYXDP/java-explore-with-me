package ru.practicum.ewm.location;

import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toLocation(LocationDto locationDto) {
        return new Location(locationDto.getLat(), locationDto.getLon());
    }

    public LocationDto toLocationDto(Location location) {
        return new LocationDto(location.getLat(), location.getLon());
    }
}
