package lt.LinasJu;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * selection functions are dedicated to extract the best parent by different techniques
 */
public class GeneticAlgorithmSelectionRepo {

    public Gene proportionateSelection(Map<Gene, Double> populationGenesWithTheirFitnessScore) {
        //mandatory sort for getting correct order by score values
        populationGenesWithTheirFitnessScore = sortByValue(populationGenesWithTheirFitnessScore);

        Double sumOfFitnessScores = populationGenesWithTheirFitnessScore.values().stream().mapToDouble(value -> value).sum();

        double probabilityToBegin = 0d;
        Map<Double, Gene> probabilitiesOfGenes = new HashMap<>();
        for (Map.Entry<Gene, Double> entry : populationGenesWithTheirFitnessScore.entrySet()) {
            probabilityToBegin += entry.getValue() / sumOfFitnessScores; //getting every Gene proportion
            probabilitiesOfGenes.put(probabilityToBegin, entry.getKey());
        }

        double randomDouble = ThreadLocalRandom.current().nextDouble(probabilityToBegin);

        for (Map.Entry<Double, Gene> entry : probabilitiesOfGenes.entrySet()) {
            Double aDouble = entry.getKey();
            if (randomDouble < aDouble) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void rankingSelection() {

    }

    public void tournamentSelection() {

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}