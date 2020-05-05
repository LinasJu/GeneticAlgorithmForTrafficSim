package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Node implements Serializable {
    private String id;
    private Float x;
    private Float y;
    private String type;
    private List<Parameter> parameters; //optional
    private Integer trafficLightLogicNo; //mandatory if type=traffic_light
}
