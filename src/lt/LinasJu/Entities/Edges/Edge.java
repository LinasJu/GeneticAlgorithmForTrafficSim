package lt.LinasJu.Entities.Edges;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Nodes.ShapePoint;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#edge_descriptions
public class Edge implements Serializable {
    private String id; //edge id
    private String from; //referenced node id
    private String to; //referenced node id
    private String type; //referenced type id from typ.xml
    private Integer numLanes; //(1 to n)
    private Float speed; //max edge speed in m/s
    private Integer priority; //used for right-of-way computation
    private List<ShapePoint> shape; //If the shape is given it should start and end with the positions of the from-node and to-node.
    private SpreadTypeEnum spreadTypeEnum;
    private List<String> allow;
    private List<String> disallow;
    private Float width; //used for visualization
    private String name; //used for visualization
    private Float endOffset; //Move the stop line back from the intersection by the given amount (effectively shortening the edge and locally enlarging the intersection)
    private Float sidewalkWidth = -1.0f; //Adds a sidewalk with the given width (defaults to -1 which adds nothing).

    private List<Lane> lanes;
}
