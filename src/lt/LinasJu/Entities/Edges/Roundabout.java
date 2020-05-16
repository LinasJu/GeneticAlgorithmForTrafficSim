package lt.LinasJu.Entities.Edges;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#roundabouts
public class Roundabout {
    List<String> edges;
    List<String> nodes;
}