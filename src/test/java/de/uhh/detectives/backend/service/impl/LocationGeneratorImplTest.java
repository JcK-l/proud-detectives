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

    private static  final Double firstRandomDouble = 900.4004227046;
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
            new Point(9.99474794474539, 53.53997072094688),
            new Point(9.994578287257859, 53.540114071758865),
            new Point(9.994598539144068, 53.54017787785358),
            new Point(9.994614413481091, 53.54000217320384),
            new Point(9.994616940451891, 53.540226147907084),
            new Point(9.994644644956649, 53.53999588723187),
            new Point(9.994539190192864, 53.5400326393949),
            new Point(9.994692717458273, 53.54010110978595),
            new Point(9.994716793939258, 53.53992437037605),
            new Point(9.99465713114555, 53.539956378447265)
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
