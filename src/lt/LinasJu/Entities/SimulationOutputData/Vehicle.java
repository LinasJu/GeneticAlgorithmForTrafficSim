package lt.LinasJu.Entities.SimulationOutputData;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Vehicle {
    private Integer id;
    private List<Float> speeds = new ArrayList<>();
    private List<Float> CO2s = new ArrayList<>();
    private List<Float> fuels = new ArrayList<>();
    private List<Float> noises = new ArrayList<>();
    private List<Float> waitings = new ArrayList<>();
    private List<String> lanes = new ArrayList<>();

    public Vehicle(Integer id) {
        this.id = id;
    }
}
