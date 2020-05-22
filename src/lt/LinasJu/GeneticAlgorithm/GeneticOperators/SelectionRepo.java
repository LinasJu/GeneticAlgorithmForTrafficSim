package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MapUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * selection functions are dedicated to extract parents from population by different techniques
 */
public class SelectionRepo {

    public List<List<Gene>> getGenePairsBySelectionType(Map<Gene, Double> populationWithFitnesses, SelectionType type) {
        List<List<Gene>> genePairs = new ArrayList<>();

        switch (type) {
            case PROPORTIONATE:
                for (int pairNo = 0; pairNo < populationWithFitnesses.size()/2; pairNo++) {
                    List<Gene> genePair = new ArrayList<>();
                    for(int i = 0; i < 2; i++) {
                        genePair.add(useProportionateSelection(populationWithFitnesses));
                    }
                    genePairs.add(genePair);
                }
                break;
            case RANKING:

            case TOURNAMENT:

            default:
                throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
        }

        return genePairs;
    }

    private Gene useProportionateSelection(Map<Gene, Double> populationWithFitnesses) {
        //mandatory sort for getting correct order by score values
        populationWithFitnesses = MapUtils.sortByValue(populationWithFitnesses);

        Double sumOfFitnessScores = populationWithFitnesses.values().stream().mapToDouble(value -> value).sum();

        double probabilityToBegin = 0d;
        Map<Gene, Double> genesWithProbability = new HashMap<>();
        for (Map.Entry<Gene, Double> entry : populationWithFitnesses.entrySet()) {
            probabilityToBegin += entry.getValue() / sumOfFitnessScores; //getting every Gene proportion
            genesWithProbability.put(entry.getKey(), probabilityToBegin);
        }

        double randomDouble = ThreadLocalRandom.current().nextDouble(probabilityToBegin);

        for (Map.Entry<Gene, Double> entry : genesWithProbability.entrySet()) {
            Double aDouble = entry.getValue();
            if (randomDouble < aDouble) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void useRankingSelection(Map<Gene, Double> populationWithFitnesses) {

    }

    private void useTournamentSelection(Map<Gene, Double> populationWithFitnesses) {

    }
}