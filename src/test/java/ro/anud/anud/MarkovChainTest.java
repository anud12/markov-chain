package ro.anud.anud;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class MarkovChainTest {

    @Test
    public void test() {
        var random = new Random(1);
        var result = new HashMap<Float, Integer>();
        var iterations = 500000;
        Stream.iterate(0, i -> i + 1)
                .limit(iterations)
                .forEach(integer -> {
                    var markovChain = new MarkovChain<Float>(random);
                    markovChain.addChoice(0.1, new MarkovChain<Float>().setValue(0.1F));
                    markovChain.addChoice(0.2, new MarkovChain<Float>().setValue(0.2F));
                    markovChain.addChoice(0.3, new MarkovChain<Float>().setValue(0.3F));
                    markovChain.addChoice(0.4, new MarkovChain<Float>().setValue(0.4F));

                    var aFloat = markovChain.getChoice().getValue();
                    result.put(aFloat, result.getOrDefault(aFloat, 0) + 1);
                });
        BiFunction<Double, Double, Boolean> epsilonEqual = (Double first, Double second) -> {
            return Math.abs((first - second)) < 0.001;
        };
        Assert.assertFalse(epsilonEqual.apply(0.1D, 0.2D));
        Assert.assertTrue(epsilonEqual.apply(0.1D, 0.1D));

        Assert.assertTrue(epsilonEqual.apply(result.get(0.1F) / (iterations + 0D), 0.1D));
        Assert.assertTrue(epsilonEqual.apply(result.get(0.2F) / (iterations + 0D), 0.1D));
        Assert.assertTrue(epsilonEqual.apply(result.get(0.3F) / (iterations + 0D), 0.1D));
        Assert.assertTrue(epsilonEqual.apply(result.get(0.4F) / (iterations + 0D), 0.7D));

        result.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(floatIntegerEntry -> {
                    System.out.println(floatIntegerEntry.getKey() + ": " + (floatIntegerEntry.getValue() / (iterations + 0D)));
                });
    }
}
