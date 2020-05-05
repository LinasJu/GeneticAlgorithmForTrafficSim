package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Connection implements Serializable {

    private String id;
    private String date;
    private String from;
    private String to;
    private Integer fromLane; //(0 to n)
    private Integer toLane; //(0 to n)
}
