package lt.LinasJu.Entities.Nodes;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#node_descriptions
public class Node extends BaseNode implements Serializable {
    private String id;
    private Float x;
    private Float y;
    private List<ShapePoint> positionList;
    private RightOfWayType rightOfWay;
    private List<String> controlledInner; //Edges which shall be controlled by a joined TLS despite being incoming as well as outgoing to the jointly controlled nodes
}
