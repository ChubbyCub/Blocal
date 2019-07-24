package com.example.blocal.model;

public class DistanceCalculator {
    private static final int R = 6371;
    private static final double MILES_KM_RATIOS = 1.6;

    public static double calculateDistanceMiles(Double lat1, Double lon1, Double lat2, Double lon2) {

        double dLat = deg2rad ( lat2 - lat1 );
        double dLon = deg2rad ( lon2 - lon1 );
        double a = Math.sin ( dLat / 2 ) * Math.sin ( dLat / 2 ) +
                Math.cos ( deg2rad ( lat1 ) ) * Math.cos ( deg2rad ( lat2 ) ) *
                        Math.sin ( dLon / 2 ) * Math.sin ( dLon / 2 );

        double c = 2 * Math.atan2 ( Math.sqrt ( a ), Math.sqrt ( 1 - a ) );
        return R * c / MILES_KM_RATIOS;
    }

    private static double deg2rad(Double deg) {
        return deg * (Math.PI / 180);
    }
}
