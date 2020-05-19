package lt.LinasJu;

public enum FilesSuffixesEnum {

    XML(".xml", null),
    NODES(".nod" + XML.toString(), "--node-files="),
    EDGES(".edg" + XML.toString(), "--edge-files="),
    CONNECTIONS(".con" + XML.toString(), "--connection-files="),
    TRAFFIC_LIGHT_LOGICS(".tll" + XML.toString(), "--tllogic-files="),
    TYPE_OF_EDGES(".typ" + XML.toString(), "--type-files="),
    NETWORK(".net" + XML.toString(), null),
    ROUTES(".rou" + XML.toString(), null),

    TAZ(".taz" + XML.toString(), null),
    OD(".od", null),
    OD_TRIPS(".odtrips" + XML.toString(), null),
    OD2TRIPS(".od2trips_config" + XML.toString(), null),

    DUAROUTER_CONFIGURATION(".trips2routes.duarcfg", null),
    SUMO_CONFIGURATION(".sumocfg", null);

    private final String suffix;
    private final String declarationCommand;

    FilesSuffixesEnum(final String suffix, String declarationCommand) {
        this.suffix = suffix;
        this.declarationCommand = declarationCommand;
    }

    @Override
    public String toString() {
        return suffix;
    }

    public String getDeclarationCommand() {
        return declarationCommand;
    }
}
