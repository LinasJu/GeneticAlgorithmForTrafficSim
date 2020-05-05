package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class Edge implements Serializable {
    private String id;
    private String edgeId;
    private Integer from;
    private Integer to;
    private Integer priority;
    private String type; //from typ.xml
    private Integer numLanes; //(1 to n)
    private Float speed; //max edge speed in m/s
    private List<EdgeShape> shapePoints; //optional
    private String spreadType; //optional
    private String width; //optional
    private List<String> allow; //optional
    private List<String> disallow; //optional
    private List<Lane> laneList; //optional

}
