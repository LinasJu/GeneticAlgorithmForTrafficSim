package lt.LinasJu.Entities.TrafficLightLogic;

//https://sumo.dlr.de/userdoc/Simulation/Traffic_Lights.html#signal_state_definitions
public enum SignalStateEnum {
    RED("r"),
    YELLOW("y"),
    GREEN("g"),
    GREEN_PRIORITY("G"),
    GREEN_RIGHT_TURN_ARROW("s"), //only on junction type traffic_light_right_on_red
    RED_AND_YELLOW("u"), // indicating of upcoming green phase but not driving yet
    OFF_BLINKING("o"), // vehicles have to yeld
    OFF_NO_SIGNAL("o"); // vehicles have the right of way

    private final String signalState;

    SignalStateEnum(final String signalState) {
        this.signalState = signalState;
    }

    @Override
    public String toString() {
        return signalState;
    }
}
