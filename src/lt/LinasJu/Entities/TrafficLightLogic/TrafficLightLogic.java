package lt.LinasJu.Entities.TrafficLightLogic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Simulation/Traffic_Lights.html#tllogic62_attributes
public class TrafficLightLogic implements Serializable {
    private int id; //This must be an existing traffic light id in the .net.xml file. Typically the id for a traffic light is identical with the junction id.
    private TrafficLightAlgorithmType tlType;
    private Integer programId;
    private Integer offset;
    private List<Phase> phases;

}
