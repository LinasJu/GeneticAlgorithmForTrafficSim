package lt.LinasJu;

public enum FilesSuffixesEnum {

    NODES(".nod.xml"),
    EDGES(".edg.xml"),
    NETWORK(".net.xml"),
    ROUTES("rou.xml"),
    SUMO_CFG(".sumocfg");
    //todo papildyti kitais reikiamais extensionais


    private final String suffix;

    FilesSuffixesEnum(final String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return suffix;
    }
}
