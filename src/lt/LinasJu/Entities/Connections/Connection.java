package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Nodes.ShapePoint;
import lt.LinasJu.Entities.TypeOfEdge.VehicleClassesEnum;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#connection_descriptions
public class Connection implements Serializable {
    private String from;
    private String to;
    private Integer fromLane; //(0 to n)
    private Integer toLane; //(0 to n)
    private boolean pass = false;
    private boolean keepClear = true;
    private Float contPos = -1f;
    private Float visibility = 4.5f;
    private Float speed = -1f;
    private List<ShapePoint> shape;
    private boolean uncontrolled = false;
    private List<VehicleClassesEnum> allow;
    private List<VehicleClassesEnum> disalow;
}
