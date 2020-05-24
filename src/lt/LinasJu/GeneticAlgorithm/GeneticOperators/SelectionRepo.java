package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * selection functions are dedicated to extract parents from population by different techniques
 */
public class SelectionRepo {

    public List<Gene> getParentGenesBySelectionType(Map<Gene, Double> populationWithFitnesses, SelectionType type) {
        List<Gene> genePair = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            switch (type) {
                case PROPORTIONATE:
                    genePair.add(useProportionateSelection(populationWithFitnesses));
                    break;
                case RANKING:
                    genePair.add(useRankingSelection(populationWithFitnesses));
                    break;
                case TOURNAMENT:
                    genePair.add(useTournamentSelection(populationWithFitnesses));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
            }
        }

        return genePair;
    }

    private Gene useProportionateSelection(Map<Gene, Double> populationWithFitnesses) {
        //mandatory sort for getting correct order by score values
        populationWithFitnesses = MapUtils.sortByValueAsc(populationWithFitnesses); //genes with best (biggest) fitness values will be in the beginning

        Double sumOfFitnessScores = populationWithFitnesses.values().stream().mapToDouble(value -> value).sum();

        double probabilityToBegin = 0d;
        Map<Gene, Double> genesWithProbability = new HashMap<>();
        for (Map.Entry<Gene, Double> entry : populationWithFitnesses.entrySet()) {
            probabilityToBegin += entry.getValue() / sumOfFitnessScores; //getting every Gene proportion
            genesWithProbability.put(entry.getKey(), probabilityToBegin);
        }

        genesWithProbability = MapUtils.sortByValueAsc(genesWithProbability);

        double randomDouble = ThreadLocalRandom.current().nextDouble(probabilityToBegin);

        for (Map.Entry<Gene, Double> entry : genesWithProbability.entrySet()) {
            Double aDouble = entry.getValue();
            if (randomDouble < aDouble) {
                return entry.getKey();
            }
        }
        return null;
    }

    private Gene useRankingSelection(Map<Gene, Double> populationWithFitnesses) {
        //mandatory sort for getting correct order by score values
        populationWithFitnesses = MapUtils.sortByValueDesc(populationWithFitnesses); //genes with worst (smallest) fitness values will be in the beginning

        Map<Integer, Gene> genesWithRank = new HashMap<>();
        int rank = 1;
        for (Map.Entry<Gene, Double> entry : populationWithFitnesses.entrySet()) {
            genesWithRank.put(rank, entry.getKey());
            rank += 1;
        }

        int sum = genesWithRank.keySet().stream().reduce(0, Integer::sum);
        int randomNumber = ThreadLocalRandom.current().nextInt(sum);

        int countingSum = 0;
        for (Map.Entry<Integer, Gene> entry : genesWithRank.entrySet()) {
            countingSum += entry.getKey();
            if (randomNumber < countingSum) {
                return entry.getValue();
            }
        }
        return null;
    }

    private Gene useTournamentSelection(Map<Gene, Double> populationWithFitnesses) {

        List<Gene> listOfGenes = new ArrayList<>(populationWithFitnesses.keySet());
        int howManyToChoose = ThreadLocalRandom.current().nextInt(3, listOfGenes.size()); //from three till all
        Map<Gene, Double> chosenGenes = new HashMap<>();

        for (int i  = 0; i < howManyToChoose; i++) {
            Gene gene = listOfGenes.get(ThreadLocalRandom.current().nextInt(1, listOfGenes.size())); //better size of list
            listOfGenes.remove(gene);
            chosenGenes.put(gene, populationWithFitnesses.get(gene));
        }

        chosenGenes = MapUtils.sortByValueAsc(chosenGenes);

        Gene geneToReturn = new Gene();
        Double genesFitness = 0d;
        for (Map.Entry<Gene, Double> entry : chosenGenes.entrySet()) {
            if(entry.getValue() > genesFitness) {
                genesFitness = entry.getValue();
                geneToReturn = entry.getKey();
            }
        }
        return geneToReturn;
    }
}