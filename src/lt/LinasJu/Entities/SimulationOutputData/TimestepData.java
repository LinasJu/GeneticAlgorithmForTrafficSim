package lt.LinasJu.Entities.SimulationOutputData;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TimestepData {
    private Float time; //the main item
    private List<TimestepVehicle> timestepVehicles;
}
