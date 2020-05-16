package lt.LinasJu.Entities;

import lombok.Getter;
import lombok.Setter;
import lt.LinasJu.Entities.Connections.Connection;
import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Roundabout;
import lt.LinasJu.Entities.Nodes.Node;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.Entities.TypeOfEdge.Type;

import java.util.List;

@Getter
@Setter
public class Network {
    List<Node> nodes;
    List<Edge> edges;
    List<Roundabout> roundabouts;
    List<Type> edgeTypes;
    List<Connection> connections;
    List<TlLogic> trafficLightLogics;
    List<Connection> trafficLightLogicsConnections;
}
