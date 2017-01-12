package net.trackme.server.domain;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Dimitri (12/01/2017)
 */
@Getter
@Builder
public class GeoLocation {

    private long timestamp;
    private double latitude, longitude;
}
