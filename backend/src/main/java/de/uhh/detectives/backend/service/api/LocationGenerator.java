package de.uhh.detectives.backend.service.api;

import org.springframework.data.geo.Point;

import java.util.List;
import java.util.Random;

public interface LocationGenerator {

    /**
     * randomly generates points in a circle
     *
     * @param center the center of the circle
     * @param radius the radius of the circle in meter
     * @param amount the amount of points that should be generated
     * @param random the random variable used for generation
     * @return the randomly generated points
     */
    List<Point> generateInCircle(final Point center, final int radius, final int amount, final Random random);
}
