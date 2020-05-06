package lt.LinasJu.Entities.Types;

//https://sumo.dlr.de/userdoc/Definition_of_Vehicles,_Vehicle_Types,_and_Routes.html#abstract_vehicle_class
public enum VehicleClassesEnum {
    IGNORING("ignoring"),
    PRIVATE("private"),
    EMERGENCY("emergency"),
    AUTHORITY("authority"),
    ARMY("army"),
    VIP("vip"),
    PEDESTRIAN("pedestrian"),
    PASSENGER("passenger"),
    HOV("hov"),
    TAXI("taxi"),
    BUS("bus"),
    COACH("coach"),
    DELIVERY("delivery"),
    TRUCK("truck"),
    TRAILER("trailer"),
    MOTORCYCLE("motorcycle"),
    MOPED("moped"),
    BICYCLE("bicycle"),
    EVEHICLE("evehicle"),
    TRAM("tram"),
    RAIL_URBAN("rail_urban"),
    RAIL("rail"),
    RAIL_ELECTRIC("rail_electric"),
    RAIL_FAST("rail_fast"),
    SHIP("ship"),
    CUSTOM1("custom1"),
    CUSTOM2("custom2");

    private final String vehicleClass;

    VehicleClassesEnum(final String vehicleClass) {
        this.vehicleClass = vehicleClass;
    }

    @Override
    public String toString() {
        return vehicleClass;
    }
}
