package ro.anud.anud;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class RainSunnyTest {

    @Test
    public void test() {

        var random = new Random();
        var rainChain = new MarkovChain<>(random);
        var sunnyChain = new MarkovChain<>(random);

        rainChain.setValue("R")
                .addChoice(0.9, rainChain)
                .addChoice(0.1, sunnyChain);


        sunnyChain.setValue("S")
                .addChoice(0.9, sunnyChain)
                .addChoice(0.1, rainChain);

        var iterations = 1000000;
        var currentChain = new AtomicReference<MarkovChain>(rainChain);
        var stringBuffer = new StringBuffer();

        var result = new HashMap<Object, Integer>();

        Stream.iterate(0, i -> i + 1)
                .limit(iterations)
                .forEach(integer -> {
                    var obj = currentChain.get().getChoice();
                    currentChain.set(obj);

                    var aFloat = obj.getValue();
                    result.put(aFloat, result.getOrDefault(aFloat, 0) + 1);

                    stringBuffer.append(obj.getValue());

                    if (integer % 80 == 0) {
                        stringBuffer.append("\n");
                    }
                });
        System.out.println(stringBuffer);

        BiFunction<Double, Double, Boolean> epsilonEqual = (Double first, Double second) -> {
            return Math.abs((first - second)) < 0.001;
        };
        Assert.assertFalse(epsilonEqual.apply(0.1D, 0.2D));
        Assert.assertTrue(epsilonEqual.apply(0.1D, 0.1D));

        Assert.assertTrue(epsilonEqual.apply(result.get("R") / (iterations + 0D), 0.5D));
        Assert.assertTrue(epsilonEqual.apply(result.get("S") / (iterations + 0D), 0.5D));
        result.entrySet()
                .forEach(floatIntegerEntry -> {
                    System.out.println(floatIntegerEntry.getKey() + ": " + (floatIntegerEntry.getValue() / (iterations + 0D)));
                });
    }
}
