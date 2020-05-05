package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Connections.Shape;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#node_descriptions
public class Node implements Serializable {
    private String id;
    private Float x;
    private Float y;
    private NodeTypesEnum type;
    private TrafficLightAlgorithmType tlType;
    private String tl; //id of traffic light program. Nodes with the same tl-value will be joined into a single program
    private Float radius = 1.5f; //only positive
    private List<Shape> positionList;
    private boolean keepClear = true;
    private RightOfWayType rightOfWay;
    private List<String> controlledInner; //Edges which shall be controlled by a joined TLS despite being incoming as well as outgoing to the jointly controlled nodes

    private List<Parameter> parameters;
    private Integer trafficLightLogicNo;
}
