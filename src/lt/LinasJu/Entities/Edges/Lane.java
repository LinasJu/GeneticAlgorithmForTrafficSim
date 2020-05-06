package lt.LinasJu.Entities.Edges;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Nodes.ShapePoint;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
//subelement of an XML-edge
public class Lane implements Serializable {
    private Integer index; //enumeration of the lane (0 is the rightmost lane, <NUMBER_LANES>-1 is the leftmost one)
    private List<String> allow;
    private List<String> disallow;
    private Float speed;
    private Float width; //used for visualisation
    private Float endOffset; //Move the stop line back from the intersection by the given amount (effectively shortening the edge and locally enlarging the intersection)
    private List<ShapePoint> shape;
    private Float acceleration; // optional
}
