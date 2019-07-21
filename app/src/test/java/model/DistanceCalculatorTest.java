package model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DistanceCalculatorTest {

    @Test
    public void calculateDistance() {
        double lat1 = 47.658404;
        double lon1 = -122.3957263;

        double lat2 = 47.6188175;
        double lon2 = -122.2007805;

        double expectedDist = 15.25;
        double actualDist = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        assertEquals(expectedDist, actualDist, 0.005);
    }
}