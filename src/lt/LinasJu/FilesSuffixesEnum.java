package lt.LinasJu;

public enum FilesSuffixesEnum {

    XML(".xml "),
    NODES(".nod" + XML.toString()),
    EDGES(".edg" + XML.toString()),
    NETWORK(".net" + XML.toString()),
    ROUTES(".rou" + XML.toString()),

    TAZ(".taz"+ XML.toString()),
    OD(".od"),
    OD_TRIPS(".odtrips" + XML.toString()),
    OD2TRIPS(".od2trips_config" + XML.toString()),

    DUAROUTER_CONFIGURATION(".trips2routes.duarcfg "),
    SUMO_CONFIGURATION(".sumocfg ");

    private final String suffix;

    FilesSuffixesEnum(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix;
    }
}
