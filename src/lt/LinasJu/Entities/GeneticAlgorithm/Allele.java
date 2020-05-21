package lt.LinasJu.Entities.GeneticAlgorithm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Allele {
    Long phaseDuration; //variable
    Long tlLogicId; //to which traffic light logic belongs phaseDuration
    Integer tlLogicsPhaseListId; //to which Phase of traffic light logic belongs phaseDuration
}
