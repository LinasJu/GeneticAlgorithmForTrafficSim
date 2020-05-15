package lt.LinasJu.Entities.TypeOfEdge;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/SUMO_edge_type_file.html#syntax
public class Type {
    private String id;
    private List<VehicleClassEnum> allow;
    private List<VehicleClassEnum> disallow;
    private boolean discard = false;
    private Long numLanes; //(1 to n)
    private boolean oneway;
    private Long priority; //used for right-of-way computation
    private Float speed; //max edge speed in m/s
    private Float sidewalkWidth = -1.0f; //Adds a sidewalk with the given width (defaults to -1 which adds nothing).
    private List<Restriction> restrictions;
}