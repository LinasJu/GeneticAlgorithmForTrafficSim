package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.TlLogics.TrafficLightAlgorithmType;

import java.io.Serializable;

@Getter
@Setter
public class BaseNode implements Serializable {
    private NodeTypesEnum type;
    private String tl; //id of traffic light program. Nodes with the same tl-value will be joined into a single program
    private TrafficLightAlgorithmType tlType;
    private Float radius = 1.5f; //only positive
    private boolean keepClear = true;

}