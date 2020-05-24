package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MathUtils;

import java.util.*;

/**
 * Crossover functions are dedicated to cross a pair of parents by different techniques to get new childs
 */
public class CrossoverRepo {

    public Gene getNewGeneByCrossoverType(List<Gene> parentGenes, CrossoverType type) {
        switch (type) {
            case PARTIALLY_MAPPED:
                return usePartiallyMappedCrossover(parentGenes);
            case CYCLE:

            case ORDER_BASED_1:

            case ORDER_BASED_2:

            case POSITION_BASED:

            case VOTING_RECOMBINATION:

            case ALTERNATING_POSITION:

            default:
                throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
        }

    }

    //crossover functions
    private Gene usePartiallyMappedCrossover(List<Gene> pairOfGenes) {
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
        Map<MapItemForDuplicates, MapItemForDuplicates> mapOne = new HashMap<>();
        Map<MapItemForDuplicates, MapItemForDuplicates> mapTwo = new HashMap<>();

        for (int alleleNo = firstCrossoverPoint; alleleNo < secondCrossoverPoint; alleleNo++) {
            Long phaseParentOne = parentOne.getDurationOfPhases().get(alleleNo).getPhaseDuration();
            Long phaseParentTwo = parentTwo.getDurationOfPhases().get(alleleNo).getPhaseDuration();

            childOne.getDurationOfPhases().get(alleleNo).setPhaseDuration(phaseParentTwo);
            mapOne.put(new MapItemForDuplicates(alleleNo, phaseParentTwo), new MapItemForDuplicates(alleleNo, phaseParentOne));

            childTwo.getDurationOfPhases().get(alleleNo).setPhaseDuration(phaseParentOne);
            mapTwo.put(new MapItemForDuplicates(alleleNo, phaseParentOne), new MapItemForDuplicates(alleleNo, phaseParentTwo));
        }

        //changing duplicates before first and after second crossover point
        for (int i = 0; i < firstCrossoverPoint; i++) {
            changeDuplicatesInChild(childOne, childTwo, mapOne, mapTwo, i);
        }
        for (int i = secondCrossoverPoint; i < childOne.getDurationOfPhases().size(); i++) {
            changeDuplicatesInChild(childOne, childTwo, mapOne, mapTwo, i);
        }

//        return Arrays.asList(childOne, childTwo);
        return childOne;
    }

    private void changeDuplicatesInChild(Gene childOne, Gene childTwo, Map<MapItemForDuplicates, MapItemForDuplicates> mapOne, Map<MapItemForDuplicates, MapItemForDuplicates> mapTwo, int index) {
        removeDuplicates(childOne, mapOne, index);
        removeDuplicates(childTwo, mapTwo, index);
    }

    private void removeDuplicates(Gene child, Map<MapItemForDuplicates, MapItemForDuplicates> mapOfDuplicates, int index) {
        List<MapItemForDuplicates> itemsRemovedDFromMap = new ArrayList<>();
        for (Map.Entry<MapItemForDuplicates, MapItemForDuplicates> entry : mapOfDuplicates.entrySet()) {
            MapItemForDuplicates mapItemForDuplicates = entry.getKey();
            MapItemForDuplicates mapItemForDuplicates2 = entry.getValue();
            if (mapItemForDuplicates.getPhaseDuration().equals(child.getDurationOfPhases().get(index).getPhaseDuration()) && !itemsRemovedDFromMap.contains(mapItemForDuplicates)) {
                child.getDurationOfPhases().get(index).setPhaseDuration(mapItemForDuplicates2.getPhaseDuration());
                itemsRemovedDFromMap.add(mapItemForDuplicates);
            }
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

@Getter
@Setter
@AllArgsConstructor
class MapItemForDuplicates {
    private Integer indexOfPlaceInGene;
    private Long PhaseDuration;
}