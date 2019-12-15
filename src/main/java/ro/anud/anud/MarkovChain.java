package ro.anud.anud;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class MarkovChain<T> {

    private List<AbstractMap.SimpleEntry<Double, MarkovChain<T>>> transitionMatrix = new ArrayList<>();
    private Random random;
    private T value;


    public MarkovChain() {
        this(new Random());
    }

    public MarkovChain(Random random) {
        this.random = random;
    }

    public MarkovChain<T> addChoice(final double weight, MarkovChain<T> t) {
        transitionMatrix.add(new AbstractMap.SimpleEntry<>(weight, t));
        return this;
    }


    public MarkovChain<T> getChoice() {
        var total = transitionMatrix.stream()
                .map(AbstractMap.SimpleEntry::getKey)
                .reduce(0D, Double::sum);

        var randomDouble = random.nextDouble();
        var percentage = randomDouble;
        var reference = new AtomicReference<>(transitionMatrix.stream()
                                                      .max(Comparator.comparingDouble(AbstractMap.SimpleEntry::getKey))
                                                      .map(AbstractMap.SimpleEntry::getValue)
                                                      .orElse(null));

        transitionMatrix
                .stream()
                .map(doubleMarkovChainEntry ->
                             new AbstractMap.SimpleEntry<>(doubleMarkovChainEntry.getKey() / total,
                                                           doubleMarkovChainEntry.getValue())
                )
                .sorted(Comparator.<AbstractMap.Entry<Double, MarkovChain<T>>>comparingDouble(AbstractMap.Entry::getKey).thenComparing((o, t1) -> {
                    return Math.toIntExact(Math.round(random.nextInt() - 0.5D));
                }))
                .takeWhile(doubleMarkovChainSimpleEntry -> {
                    if (doubleMarkovChainSimpleEntry.getKey() >= percentage) {
                        reference.set(doubleMarkovChainSimpleEntry.getValue());
                        return false;
                    }
                    return true;

                })
                .collect(Collectors.toList());
        return reference.get();
    }

    public T getValue() {
        return value;
    }

    public MarkovChain<T> setValue(final T value) {
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
