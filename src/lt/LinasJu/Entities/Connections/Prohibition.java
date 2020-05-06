package lt.LinasJu.Entities.Connections;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//https://sumo.dlr.de/userdoc/Networks/PlainXML.html#setting_connection_priorities
public class Prohibition {
    private String prohibitor;//<prohibiting_from_edge>-><prohibiting_to_edge>
    private String prohibited;//<prohibited_from_edge>-><prohibited_to_edge>

}