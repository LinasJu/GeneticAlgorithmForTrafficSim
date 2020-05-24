package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lt.LinasJu.Entities.GeneticAlgorithm.Allele;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MathUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutationRepo {
    //mutation functions

    public void mutateGeneByMutationType(Gene gene, MutationType type) {
        switch (type) {
            case DISPLACEMENT:
                useDisplacementMutation(gene);
                break;
            case EXCHANGE:
                useExchangeMutation(gene);
                break;
            case INSERTION:
                useInsertionMutation(gene);
                break;
            case SIMPLE_INVERSION:
                useSimpleInversionMutation(gene);
                break;
            case INVERSION:
                useInversionMutation(gene);
                break;
            case SCRAMBLE:
                useScrambleMutation(gene);
                break;
            default:
                throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
        }
    }

    private void useDisplacementMutation(Gene gene) {

        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        int firstPoint = crossoverPoints.get(0);
        int secondPoint = crossoverPoints.get(1);

        List<Allele> sublistToBeDisplaced = new ArrayList<>(gene.getDurationOfPhases().subList(firstPoint, secondPoint));
        gene.getDurationOfPhases().subList(firstPoint, secondPoint).clear();

        int place = MathUtils.getRandomIntSmallerThan(gene.getDurationOfPhases().size());
        gene.getDurationOfPhases().addAll(place, sublistToBeDisplaced);
    }

    private void useExchangeMutation(Gene gene) {
        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        Collections.swap(gene.getDurationOfPhases(), crossoverPoints.get(0), crossoverPoints.get(1));
    }

    private void useInsertionMutation(Gene gene) {
        Integer takenFrom = MathUtils.getRandomIntSmallerThan(gene.getDurationOfPhases().size());
        Allele extractedAllele = gene.getDurationOfPhases().get(takenFrom);
        gene.getDurationOfPhases().remove(extractedAllele);

        Integer putInPlace = MathUtils.getRandomIntSmallerThan(gene.getDurationOfPhases().size());
        gene.getDurationOfPhases().add(putInPlace, extractedAllele);
    }

    private void useSimpleInversionMutation(Gene gene) {
        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        int firstPoint = crossoverPoints.get(0);
        int secondPoint = crossoverPoints.get(1);

        List<Allele> sublistToMutate = new ArrayList<>(gene.getDurationOfPhases().subList(firstPoint, secondPoint));
        Collections.reverse(sublistToMutate);

        sublistToMutate.forEach(alleleToRemove -> gene.getDurationOfPhases().remove(alleleToRemove));
        gene.getDurationOfPhases().addAll(firstPoint, sublistToMutate);
    }

    private void useInversionMutation(Gene gene) {
        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        int firstPoint = crossoverPoints.get(0);
        int secondPoint = crossoverPoints.get(1);

        List<Allele> sublistToMutate = new ArrayList<>(gene.getDurationOfPhases().subList(firstPoint, secondPoint));
        Collections.reverse(sublistToMutate);
        sublistToMutate.forEach(alleleToRemove -> gene.getDurationOfPhases().remove(alleleToRemove));

        gene.getDurationOfPhases().addAll(MathUtils.getRandomIntSmallerThan(gene.getDurationOfPhases().size()), sublistToMutate);
    }

    private void useScrambleMutation(Gene gene) {
        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        int firstPoint = crossoverPoints.get(0);
        int secondPoint = crossoverPoints.get(1);

        List<Allele> sublistToMutate = new ArrayList<>(gene.getDurationOfPhases().subList(firstPoint, secondPoint));
        Collections.shuffle(sublistToMutate);

        sublistToMutate.forEach(alleleToRemove -> gene.getDurationOfPhases().remove(alleleToRemove));
        gene.getDurationOfPhases().addAll(firstPoint, sublistToMutate);
    }
}
