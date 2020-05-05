package lt.LinasJu.Entities.TrafficLightLogic;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TrafficLightLogic implements Serializable {
    private int id;
    private String type;
    private Integer programId;
    private Integer offset;
    private List<Phase> phases;

}
