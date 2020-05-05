package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Lane implements Serializable {

    private Integer index;
    private Float acceleration; // optional
}
