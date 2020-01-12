package ro.anud.markovchain;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Choice<T> {

    private List<Map.Entry<Double, T>> list = new ArrayList<>();

    private Random random;

    public Choice() {
        this(new Random());
    }

    public Choice(Random random) {
        this.random = random;
    }

    public Choice<T> addChoice(double chance, T t) {
        list.add(new AbstractMap.SimpleEntry<>(chance, t));
        return this;
    }

    public T chose() {
        var total = list.stream()
                .map(Map.Entry::getKey)
                .reduce(0D, Double::sum);

        var randomDouble = random.nextDouble();
        var percentage = randomDouble;

        var comparator = Comparator
                .<Map.Entry<Double, T>>comparingDouble(Map.Entry::getKey)
                .thenComparing(
                        (o, t1) -> Math.toIntExact(Math.round(random.nextInt() - 0.5D))
                );

        var sortedTransitionMatrix = list
                .stream()
                .map(doubleMarkovChainEntry ->
                             new AbstractMap.SimpleEntry<>(doubleMarkovChainEntry.getKey() / total,
                                                           doubleMarkovChainEntry.getValue()) {}
                )
                .sorted(comparator)
                .collect(Collectors.toList());

        var reference = new AtomicReference<>(sortedTransitionMatrix.stream()
                                                      .max(comparator)
                                                      .map(AbstractMap.SimpleEntry::getValue)
                                                      .orElseThrow(NullPointerException::new));

        sortedTransitionMatrix
                .stream()
                .limit(sortedTransitionMatrix.size() - 1)
                .takeWhile(doubleMarkovChainSimpleEntry -> {
                    if (doubleMarkovChainSimpleEntry.getKey() >= percentage) {
                        reference.set(doubleMarkovChainSimpleEntry.getValue());
                        return false;
                    }
                    return true;

                })
                .forEach(doubleTSimpleEntry -> {});

        return reference.get();
    }
}
