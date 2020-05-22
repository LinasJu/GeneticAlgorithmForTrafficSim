package lt.LinasJu.GeneticAlgorithm.GeneticOperators;

import lt.LinasJu.Entities.GeneticAlgorithm.Allele;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Utils.MathUtils;

import java.util.List;

public class MutationRepo {
    //mutation functions

    public void mutateGeneByMutationType(Gene gene, MutationType type) {
        switch (type) {
            case DISPLACEMENT:
                useDisplacementMutation(gene);
                break;
            case EXCHANGE:

            case INSERTION:

            case SIMPLE_INVERSION:

            case INVERSION:

            case SCRAMBLE:

            default:
                throw new IllegalStateException("Unexpected value or not yet implemented: " + type);
        }
    }

    private void useDisplacementMutation(Gene gene) {

        List<Integer> crossoverPoints = MathUtils.getTwoRandomIntPointsSmallerThan(gene.getDurationOfPhases().size());
        int firstPoint = crossoverPoints.get(0);
        int secondPoint = crossoverPoints.get(1);

        List<Allele> sublistToBeDisplaced = gene.getDurationOfPhases().subList(firstPoint, secondPoint);
        gene.getDurationOfPhases().subList(firstPoint, secondPoint).clear();

        gene.getDurationOfPhases().addAll(MathUtils.getRandomIntSmallerThan(gene.getDurationOfPhases().size()), sublistToBeDisplaced);
    }

    private void useExchangeMutation(Gene gene) {

    }

    private void useInsertionMutation(Gene gene) {

    }

    private void useSimpleInversionMutation(Gene gene) {

    }

    private void useInversionMutation(Gene gene) {

    }

    private void useScrambleMutation(Gene gene) {

    }
}
