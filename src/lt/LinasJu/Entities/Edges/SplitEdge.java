package lt.LinasJu.Entities.Edges;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Nodes.BaseNode;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#road_segment_refining
//subelement of an XML-edge
public class SplitEdge extends BaseNode implements Serializable {
    private Float pos; //if negative position - split is inserted counting from the end of the edge
    private List<Integer> lanes;
    private Float speed;
    private String id; //new node id
    private String idBefore;
    private String idAfter;
}