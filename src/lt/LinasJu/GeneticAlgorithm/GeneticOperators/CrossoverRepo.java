package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MathUtils;

import java.util.*;

/**
 * Crossover functions are dedicated to cross a pair of parents by different techniques to get new childs
 */
public class CrossoverRepo {

    public List<Gene> getNewPopulationOfGenesByCrossoverType(List<List<Gene>> listOfGenePairs, CrossoverType type) {
        List<Gene> newPopulation = new ArrayList<>();
        switch (type) {
            case PARTIALLY_MAPPED:
                listOfGenePairs.forEach(pairOfGenes -> newPopulation.addAll(usePartiallyMappedCrossover(pairOfGenes)));
                break;
            case CYCLE:

            case ORDER_BASED_1:

            case ORDER_BASED_2:

            case POSITION_BASED:

            case VOTING_RECOMBINATION:

            case ALTERNATING_POSITION:

            default:
                throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
        }

        return newPopulation;
    }

    //crossover functions
    private List<Gene> usePartiallyMappedCrossover(List<Gene> pairOfGenes) {
        Gene parentOne = pairOfGenes.get(0);
        Gene parentTwo = pairOfGenes.get(1);

        if (parentOne.getDurationOfPhases().size() != parentTwo.getDurationOfPhases().size()) {
            throw new IllegalArgumentException("Cannot perform partially-mapped crossover with different length parents.");
        }

        //genes size must be equal
        int sizeOfGenes = parentOne.getDurationOfPhases().size();

        //generating random beginning and end of gene alleles to cross
        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(sizeOfGenes);
        int firstCrossoverPoint = crossoverPoints.get(0);
        int secondCrossoverPoint = crossoverPoints.get(1);


        Gene childOne = new Gene(parentOne.getDurationOfPhases());
        Gene childTwo = new Gene(parentTwo.getDurationOfPhases());

        //maps to make path for replacing duplicates
        Map<Long, Long> mapOne = new HashMap<>();
        Map<Long, Long> mapTwo = new HashMap<>();

        for (int alleleNo = firstCrossoverPoint; alleleNo < secondCrossoverPoint; alleleNo++) {
            Long phaseParentOne = parentOne.getDurationOfPhases().get(alleleNo).getPhaseDuration();
            Long phaseParentTwo = parentTwo.getDurationOfPhases().get(alleleNo).getPhaseDuration();

            childOne.getDurationOfPhases().get(alleleNo).setPhaseDuration(phaseParentTwo);
            mapOne.put(phaseParentTwo, phaseParentOne);

            childTwo.getDurationOfPhases().get(alleleNo).setPhaseDuration(phaseParentOne);
            mapTwo.put(phaseParentOne, phaseParentTwo);
        }

        //changing duplicates before first and after second crossover point
        for (int i = 0; i < firstCrossoverPoint; i++) {
            changeDuplicatesInChild(childOne, childTwo, mapOne, mapTwo, i);
        }
        for (int i = secondCrossoverPoint; i < childOne.getDurationOfPhases().size(); i++) {
            changeDuplicatesInChild(childOne, childTwo, mapOne, mapTwo, i);
        }

        return Arrays.asList(childOne, childTwo);
    }

    private void changeDuplicatesInChild(Gene childOne, Gene childTwo, Map<Long, Long> mapOne, Map<Long, Long> mapTwo, int index) {
        while (mapOne.get(childOne.getDurationOfPhases().get(index).getPhaseDuration()) != null) {
            childOne.getDurationOfPhases().get(index).setPhaseDuration(mapOne.get(childOne.getDurationOfPhases().get(index).getPhaseDuration()));
        }

        while (mapTwo.get(childTwo.getDurationOfPhases().get(index).getPhaseDuration()) != null) {
            childTwo.getDurationOfPhases().get(index).setPhaseDuration(mapTwo.get(childOne.getDurationOfPhases().get(index).getPhaseDuration()));
        }
    }

    private void useCycleCrossover(List<Gene> pairOfGenes) {

    }

    private void useOrderBasedCrossover1(List<Gene> pairOfGenes) {

    }

    private void useOrderBasedCrossover2(List<Gene> pairOfGenes) {

    }

    private void usePositionBasedCrossover(List<Gene> pairOfGenes) {

    }

    private void useVotingRecombinationCrossover(List<Gene> allGenes) {

    }

    private void useAlternatingPositionCrossover(List<Gene> allGenes) {

    }
}
