package net.trackme.server.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * @author Dimitri (10/01/2017)
 */
@Getter
@Builder
public class Trip {

    private String _id;
    private List<GeoLocation> geoLocations;
}
