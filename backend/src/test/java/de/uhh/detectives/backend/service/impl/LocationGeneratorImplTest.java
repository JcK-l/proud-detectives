package de.uhh.detectives.backend.service.impl;

import de.uhh.detectives.backend.service.api.LocationGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
public class LocationGeneratorImplTest {

    private static final Double firstRandomDouble = 900.4004227046;
    private static final Double[] randomDoubles = new Double[] {
            726.2895275915, 1105.499235166, 28.2679072853, 1411.0204422618,                 // distances
            222.3241122218, 490.7678870255, 804.6275513687, 848.3999673177, 427.4845146097, // distances
            104.0531220925, 343.3049898624, 355.8773095654, 129.6290763266, 1.538703203,    // angles
            105.1550078877, 291.0515765851, 40.3728903543, 127.3124172251, 136.5055716641   // angles
    };

    private static final LocationGenerator testee = new LocationGeneratorImpl();

    @Test
    public void testGenerate() {
        // given
        final Point center = new Point(9.994611d, 53.540005d);
        final int radius = 2000;
        final int amount = 10;
        final Random random = mock(Random.class, withSettings().withoutAnnotations());
        when(random.nextDouble(anyDouble(), anyDouble())).thenReturn(firstRandomDouble, randomDoubles);

        final Point[] pointsArray = new Point[] {
                new Point(10.007838704127186, 53.53803726955933),
                new Point(9.99145061150512, 53.54626607784871),
                new Point(9.99340704852899, 53.54992875763334),
                new Point(9.994940727463357, 53.53984273259902),
                new Point(9.995184996212444, 53.55269961753237),
                new Point(9.997860929136815, 53.53948189712467),
                new Point(9.987674197628989, 53.54159159221192),
                new Point(10.002505611030712, 53.5455220179535),
                new Point(10.004829157589795, 53.53537659428126),
                new Point(9.999066791835823, 53.537213962888366)
        };
        final List<Point> expected = Arrays.asList(pointsArray);

        // when
        final List<Point> actual = testee.generateInCircle(center, radius, amount, random);

        // then
        for (int i = 0; i < pointsArray.length; i++) {
            assertEquals(expected.get(i).getX(), actual.get(i).getX());
            assertEquals(expected.get(i).getY(), actual.get(i).getY());
        }
    }
}
