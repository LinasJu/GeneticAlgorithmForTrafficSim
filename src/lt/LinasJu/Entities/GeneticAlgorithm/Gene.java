package lt.LinasJu.Entities.GeneticAlgorithm;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gene {
    List<Allele> durationOfPhases;
}
