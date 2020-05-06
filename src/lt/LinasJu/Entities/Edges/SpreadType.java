package lt.LinasJu.Entities.Edges;

//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#spreadtype
public enum SpreadType {
    RIGHT("right"),
    CENTER("center"),
    ROAD_CENTER("roadCenter");

    private final String spreadType;

    SpreadType(String spreadType) {
        this.spreadType = spreadType;
    }

    @Override
    public String toString() {
        return spreadType;
    }
}