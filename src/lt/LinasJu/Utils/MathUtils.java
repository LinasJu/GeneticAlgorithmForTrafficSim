package lt.LinasJu.Utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MathUtils {

    /**
     *
     * @param maxInt The biggest integer for scope for random numbers generation
     * @return List of two integers, first is smaller than second.
     */
    public static List<Integer> getTwoRandomIntPointsSmallerThan(int maxInt) {
        int firstPoint = getRandomIntSmallerThan(maxInt);
        int secondPoint = getRandomIntSmallerThan(maxInt);
        //if happens, that beggining and end of alleles is the same, generate till not the same
        while (firstPoint == secondPoint) {
            secondPoint = getRandomIntSmallerThan(maxInt);
        }

        List<Integer> points = Arrays.asList(firstPoint, secondPoint);

        //changing order - smaller goes first
        Collections.sort(points);
        return points;
    }

    public static Integer getRandomIntSmallerThan(int maxInt) {
        return ThreadLocalRandom.current().nextInt(maxInt);
    }
}
