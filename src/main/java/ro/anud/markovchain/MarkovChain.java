package ro.anud.markovchain;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MarkovChain<U> {
    class CustomEntry<U> extends AbstractMap.SimpleEntry<Double, Supplier<MarkovChain<U>>> {
        public CustomEntry(final Double key, final Supplier<MarkovChain<U>> value) {
            super(key, value);
        }
    }

    private List<CustomEntry<U>> transitionMatrix = new ArrayList<>();
    private Random random;
    private U value;


    public MarkovChain() {
        this(new Random());
    }

    public MarkovChain(Random random) {
        this.random = random;
    }

    public MarkovChain<U> addChoice(final double weight, Supplier<MarkovChain<U>> t) {
        transitionMatrix.add(new CustomEntry<>(weight, t));
        return this;
    }


    public MarkovChain<U> getChoice() {
        var total = transitionMatrix.stream()
                .map(CustomEntry<U>::getKey)
                .reduce(0D, Double::sum);

        var randomDouble = random.nextDouble();
        var percentage = randomDouble;

        var comparator = Comparator
                .comparingDouble(CustomEntry<U>::getKey)
                .thenComparing(
                        (o, t1) -> Math.toIntExact(Math.round(random.nextInt() - 0.5D))
                );

        var sortedTransitionMatrix = transitionMatrix
                .stream()
                .map(doubleMarkovChainEntry ->
                             new CustomEntry<>(doubleMarkovChainEntry.getKey() / total,
                                               doubleMarkovChainEntry.getValue())
                )
                .sorted(comparator)
                .collect(Collectors.toList());

        var reference = new AtomicReference<>(sortedTransitionMatrix.stream()
                                                      .max(comparator)
                                                      .map(CustomEntry::getValue)
                                                      .map(Supplier::get)
                                                      .orElse(null));

        sortedTransitionMatrix
                .stream()
                .limit(sortedTransitionMatrix.size() - 1)
                .takeWhile(doubleMarkovChainSimpleEntry -> {
                    if (doubleMarkovChainSimpleEntry.getKey() >= percentage) {
                        reference.set(doubleMarkovChainSimpleEntry.getValue().get());
                        return false;
                    }
                    return true;

                })
                .collect(Collectors.toList());

        return reference.get();
    }

    public U getValue() {
        return value;
    }

    public MarkovChain<U> setValue(final U value) {
        this.value = value;
        return this;
    }


    @Override
    public String toString() {
        return "MarkovChain{" +
                "random=" + random +
                ", value=" + value +
                '}';
    }
}
