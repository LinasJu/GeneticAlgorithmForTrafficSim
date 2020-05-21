package lt.LinasJu;

import lt.LinasJu.Entities.GeneticAlgorithm.Allele;
import lt.LinasJu.Entities.GeneticAlgorithm.Gene;
import lt.LinasJu.Entities.SimulationOutputData.Vehicle;
import lt.LinasJu.Entities.TlLogics.Phase;
import lt.LinasJu.Entities.TlLogics.TlLogic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GeneticAlgorithmRepo {

    private Long maxPhaseDuration = 300L;
    private Long minPhaseDuration = 1L;
    private Integer sizeOfPopulation = 20;
    private static double mutationRate = 0.05;
    private static double crossoverRate = 1; //optional. usually crossover is always applied



    //the lowest fitness is the best
    public Float calculatefitness(List<Vehicle> vehicles) {
        Float sum = 0f;
        for (Vehicle vehicle : vehicles) {
            for (Float waiting : vehicle.getWaitings()) {
                sum += waiting;
            }
        }
        return sum;
    }

    /**
     * Function dedicated to convert tlLogic List to Gene for further edition with Genetic algorithm
     * @param tlLogics
     * @return
     */
    public Gene convertTlLogicsToGene(List<TlLogic> tlLogics) {
        Gene gene = new Gene();
        for (TlLogic tlLogic : tlLogics) {
            for (int i = 0; i < tlLogic.getPhase().size(); i++) {
                Phase phase = tlLogic.getPhase().get(i);
                Allele allele = new Allele();
                allele.setPhaseDuration(phase.getDuration());
                allele.setTlLogicId(tlLogic.getId());
                allele.setTlLogicsPhaseListId(i);
            }
        }
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

        //todo check parsing of map ^^^^
        for (TlLogic tlLogic : tlLogics) {
            for (int i = 0; i < tlLogic.getPhase().size(); i++) {
                Map<Integer, Long> samePhaseIdDurations = geneTlLogicIdPhaseIdDurationMap.get(tlLogic.getId());
                tlLogic.getPhase().get(i).setDuration(samePhaseIdDurations.get(i));
            }
        }

        return tlLogics;
    }

    private List<Allele> generateNewRandomDurations(List<Allele> alleles) {
        alleles.forEach(allele -> allele.setPhaseDuration(generateRandomDurationInRange()));
        return alleles;
    }

    public List<Gene> getRandomPopulationOfGenesByTlLogics(List<TlLogic> tlLogics, int sizeOfPopulation) {
        List<Gene> genes = new ArrayList<>();
        Gene baseGene = convertTlLogicsToGene(tlLogics);

        genes.add(baseGene); //first gene with be from TlLogics

        for (int i = 1; i < sizeOfPopulation; i++) {
            baseGene.setDurationOfPhases(generateNewRandomDurations(baseGene.getDurationOfPhases()));
            genes.add(baseGene);
        }
        return genes;
    }

    private Long generateRandomDurationInRange() {
        return ThreadLocalRandom.current().nextLong(minPhaseDuration, maxPhaseDuration + 1);
    }
}
