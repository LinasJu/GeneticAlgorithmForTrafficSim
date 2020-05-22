package lt.LinasJu.GeneticAlgorithm;

import lt.LinasJu.Entities.GeneticAlgorithm.Allele;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import lt.LinasJu.Entities.TlLogics.Phase;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.GeneticAlgorithm.GeneticOperators.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GeneticAlgorithmRepo {

    SelectionRepo gaSelectionRepo = new SelectionRepo();
    CrossoverRepo gaCrossoverRepo = new CrossoverRepo();
    MutationRepo gaMutationRepo = new MutationRepo();

    private Long maxPhaseDuration = 300L;
    private Long minPhaseDuration = 1L;
    private static double mutationRate = 0.1;
    private static double crossoverRate = 1; //optional. usually crossover is always applied

    //the lowest fitness is the best
    public Double calculatefitness(List<Vehicle> vehicles) {
        Double sum = 0d;
        for (Vehicle vehicle : vehicles) {
            for (Float waiting : vehicle.getWaitings()) {
                sum += waiting;
            }
        }
        return sum == 0 ? Double.POSITIVE_INFINITY :  1/sum;
    }

    /**
     * Function dedicated to convert tlLogic List to Gene for further edition with Genetic algorithm
     * @param tlLogics
     * @return
     */
    public Gene convertTlLogicsToGene(List<TlLogic> tlLogics) {
        Gene gene = new Gene();
        List<Allele> alleles = new ArrayList<>();
        for (TlLogic tlLogic : tlLogics) {
            for (int i = 0; i < tlLogic.getPhase().size(); i++) {
                Phase phase = tlLogic.getPhase().get(i);
                Allele allele = new Allele(phase.getDuration(), tlLogic.getId(), i);
                alleles.add(allele);
            }
        }
        gene.setDurationOfPhases(alleles);
        return gene;
    }

    /**
     * Function dedicated to assert new gene phase duration values to TlLogic list for further simulation
     * @param gene The gene from which phase duration values will be set
     * @param tlLogics List of TlLogic which phase will get new Duration values
     * @return List of TlLogic with new phase durations
     */
    public List<TlLogic> setNewTlLogicsPhaseDurationsWithGeneValues(Gene gene, List<TlLogic> tlLogics) {
        List<Allele> geneAlleles = gene.getDurationOfPhases();
        Map<Long, Map<Integer, Long>> geneTlLogicIdPhaseIdDurationMap = new HashMap<>();

        for (Allele allele : geneAlleles) {
            geneTlLogicIdPhaseIdDurationMap.computeIfAbsent(allele.getTlLogicId(), k -> new HashMap<>());

            Map<Integer, Long> phaseIdDurationMap = geneTlLogicIdPhaseIdDurationMap.get(allele.getTlLogicId());
            phaseIdDurationMap.computeIfAbsent(allele.getTlLogicsPhaseListId(), k -> allele.getPhaseDuration());
        }

        for (TlLogic tlLogic : tlLogics) {
            for (int i = 0; i < tlLogic.getPhase().size(); i++) {
                Map<Integer, Long> samePhaseIdDurations = geneTlLogicIdPhaseIdDurationMap.get(tlLogic.getId());
                tlLogic.getPhase().get(i).setDuration(samePhaseIdDurations.get(i));
            }
        }

        return tlLogics;
    }

    private List<Allele> generateNewRandomDurations(List<Allele> alleles) {
        List<Allele> newAlleles = new ArrayList<>();
        alleles.forEach(allele -> {
            Allele newAllele = new Allele(generateRandomDurationInRange(), allele.getTlLogicId(), allele.getTlLogicsPhaseListId());
            newAlleles.add(newAllele);
        });
        return newAlleles;
    }

    public List<Gene> getRandomPopulationOfGenesByTlLogics(List<TlLogic> tlLogics, int sizeOfPopulation) {
        List<Gene> genes = new ArrayList<>();
        Gene baseGene = convertTlLogicsToGene(tlLogics);

        genes.add(baseGene); //first gene will be from TlLogics

        for (int i = 1; i < sizeOfPopulation; i++) {
            Gene newRandomGene = new Gene();
            newRandomGene.setDurationOfPhases(generateNewRandomDurations(baseGene.getDurationOfPhases()));
            genes.add(newRandomGene);
        }
        return genes;
    }

    private Long generateRandomDurationInRange() {
        return ThreadLocalRandom.current().nextLong(minPhaseDuration, maxPhaseDuration + 1);
    }

    //modifying gene population for trying to get better fitness score
    public List<Gene> modifyPopulationOfGenes(Map<Gene, Double> populationGenesWithTheirFitnessScore) {

        List<List<Gene>> listOfGenePairs = gaSelectionRepo.getGenePairsBySelectionType(populationGenesWithTheirFitnessScore, SelectionType.PROPORTIONATE);

        List<Gene> newPopulation = gaCrossoverRepo.getNewPopulationOfGenesByCrossoverType(listOfGenePairs, CrossoverType.PARTIALLY_MAPPED);

        newPopulation.forEach(gene -> {
            if (ThreadLocalRandom.current().nextDouble() <= mutationRate) {
                gaMutationRepo.mutateGeneByMutationType(gene, MutationType.DISPLACEMENT);
            }
        });

        return newPopulation;
    }
}
