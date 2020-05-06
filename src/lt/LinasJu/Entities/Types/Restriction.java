package lt.LinasJu.Entities.Types;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Restriction {
    private VehicleClassesEnum vehicleClass;
    private Float speed;
}
