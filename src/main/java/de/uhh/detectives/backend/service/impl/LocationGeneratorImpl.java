package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.service.api.LocationGenerator;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class LocationGeneratorImpl implements LocationGenerator {

    private static final double MIN_DISTANCE = 10.0d;
    private static final double MIN_ANGLE_IN_DEGREE = 0d;
    private static final double MAX_ANGLE_IN_DEGREE = 360d;

    @Override
    public List<Point> generateInCircle(final Point center, final int radius, final int amount, final Random random) {
        // generate random distances
        final List<Double> distancesInMeters = generateDistances(radius, amount, random);
        // generate random angles
        final List<Double> anglesInDegrees = generateAngles(amount, random);

        // calculate points based on center point, distance and angle
        return calculatePoints(center, distancesInMeters, anglesInDegrees);
    }

    private List<Double> generateDistances(final int radius, final int amount, final Random random) {
        final List<Double> distances = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final double randomDistance = random.nextDouble(MIN_DISTANCE, radius);
            distances.add(randomDistance);
        }
        return distances;
    }

    private List<Double> generateAngles(final int amount, final Random random) {
        final List<Double> angles = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            final double randomAngle = random.nextDouble(MIN_ANGLE_IN_DEGREE, MAX_ANGLE_IN_DEGREE);
            angles.add(randomAngle);
        }
        return angles;
    }

    private List<Point> calculatePoints(final Point center, final List<Double> distances, final List<Double> angles) {
        final List<Point> points = new ArrayList<>();
        for (int i = 0; i < distances.size(); i++) {
            points.add(calculatePoint(center, distances.get(i), angles.get(i)));
        }
        return points;
    }

    private Point calculatePoint(final Point center, final Double distance, final Double angle) {

        final double deltaX = calculateDeltaX(distance, angle);
        final double deltaY = calculateDeltaY(distance, angle);

        final double longitude = center.getX() + deltaX / (Metrics.KILOMETERS.getMultiplier() * 1000);
        final double latitude = center.getY() + deltaY / (Metrics.KILOMETERS.getMultiplier() * 1000);

        return new Point(longitude, latitude);
    }

    private double calculateDeltaX(final Double distance, final Double angle) {
        return Math.sin(angle * Math.PI / 180) * distance;
    }

    private double calculateDeltaY(final Double distance, final Double angle) {
        return Math.cos(angle * Math.PI / 180) * distance;
    }

}
