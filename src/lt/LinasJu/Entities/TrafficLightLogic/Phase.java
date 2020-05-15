package lt.LinasJu.Entities.TrafficLightLogic;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Simulation/Traffic_Lights.html#phase62_attributes
public class Phase {
    private Long duration;
    private List<SignalStateEnum> states; //https://sumo.dlr.de/userdoc/Simulation/Traffic_Lights.html#default_link_indices
    private Long minDur;
    private Long maxDur;
    private String name;
    private List<Integer> next;
}
