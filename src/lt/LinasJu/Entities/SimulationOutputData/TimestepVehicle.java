package lt.LinasJu.Entities.SimulationOutputData;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class TimestepVehicle implements Serializable {
    private Integer id;
    private Float speed;
    private Float CO2;
    private Float fuel;
    private Float noise;
    private Float waiting;
    private String lane;
}
