package lt.LinasJu.Entities.GeneticAlgorithm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Allele {
    Long phaseDuration; //variable
    Long tlLogicId; //to which traffic light logic belongs phaseDuration
    Integer tlLogicsPhaseListId; //to which Phase of traffic light logic belongs phaseDuration
}
