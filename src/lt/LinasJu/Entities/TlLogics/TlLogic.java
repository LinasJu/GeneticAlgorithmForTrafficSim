package lt.LinasJu.Entities.TlLogics;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Simulation/Traffic_Lights.html#tllogic62_attributes
public class TlLogic implements Serializable {
    private Long id; //This must be an existing traffic light id in the .net.xml file. Typically the id for a traffic light is identical with the junction id.
    private TrafficLightAlgorithmType type; //type of traffic light
    private Long programID;
    private Long offset;
    private List<Phase> phase;

}
