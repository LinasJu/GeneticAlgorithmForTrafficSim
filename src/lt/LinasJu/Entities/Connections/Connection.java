package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Nodes.ShapePoint;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#connection_descriptions
public class Connection implements Serializable {
    private String from;
    private String to;
    private Long fromLane; //(0 to n)
    private Long toLane; //(0 to n)
    private boolean pass = false;
    private boolean keepClear = true;
    private Float contPos = -1f;
    private Float visibility = 4.5f;
    private Float speed = -1f;
    private List<ShapePoint> shape;
    private boolean uncontrolled = false;
    private List<String> allow;
    private List<String> disallow;

    //speficically for tll file connections
    private String tl;//id ot the traffic light which controls this connection
    private Long linkIndex; //the index in the state-attribute of the <phase>-elements which controls this connection
}
