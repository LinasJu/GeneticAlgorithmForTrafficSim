package lt.LinasJu;

import lt.LinasJu.Entities.Connections.Connection;
import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Lane;
import lt.LinasJu.Entities.Edges.Roundabout;
import lt.LinasJu.Entities.Edges.SpreadTypeEnum;
import lt.LinasJu.Entities.Nodes.NodeTypesEnum;
import lt.LinasJu.Entities.Nodes.ShapePoint;
import lt.LinasJu.Entities.TlLogics.Phase;
import lt.LinasJu.Entities.TlLogics.SignalStateEnum;
import lt.LinasJu.Entities.TlLogics.TlLogic;
import lt.LinasJu.Entities.TlLogics.TrafficLightAlgorithmType;
import lt.LinasJu.Entities.TypeOfEdge.Restriction;
import lt.LinasJu.Entities.TypeOfEdge.Type;
import lt.LinasJu.Entities.TypeOfEdge.VehicleClassEnum;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.*;
import java.util.stream.Collectors;

public class ParserRepo {
    /**
     * @param doc a document to be parsed. must be data with tags from xml file.
     * @return nodes map with all their attributes and additional lower level attributes
     */
    public Map<String, List<Map<String, Object>>> parseDocumentToObjects(Document doc) {
        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        Node node = doc.getDocumentElement().getFirstChild();

        return parseAllNodes(node);
    }

    private Map<String, Object> getNodeAttributes(NamedNodeMap mapAttributes) {
        Map<String, Object> nodeMap = new HashMap<>();
        for (int i = 0; i < mapAttributes.getLength(); i++) {
            Node node = mapAttributes.item(i);
            nodeMap.put(node.getNodeName(), node.getNodeValue());
        }

        return nodeMap;
    }

    /**
     * recursive function to find all node parameters including lower level sub-nodes
     *
     * @param node the node that will be checked for attributes and children sub-nodes
     * @return all parsed nodes, sorted by node type
     */
    private Map<String, List<Map<String, Object>>> parseAllNodes(Node node) {
        Map<String, List<Map<String, Object>>> nodes = new HashMap<>();
        while (node != null) {
            if (node.hasAttributes()) {
                Map<String, Object> allNodeAttributes = getNodeAttributes(node.getAttributes());

                if (node.getFirstChild() != null) { //checks for sub-nodes
                    Node subNode = node.getFirstChild().getNextSibling();
                    allNodeAttributes.put(subNode.getNodeName(), parseAllNodes(subNode).get(subNode.getNodeName()));
                }

                getNodesListByNodeName(nodes, node.getNodeName()).add(allNodeAttributes);
            }
            node = node.getNextSibling();
        }
        return nodes;
    }

    private List<Map<String, Object>> getNodesListByNodeName(Map<String, List<Map<String, Object>>> nodes, String nodeKey) {
        if (nodes.get(nodeKey) != null) {
            return nodes.get(nodeKey);
        }
        nodes.put(nodeKey, new ArrayList<>());
        return nodes.get(nodeKey);
    }

    public List<lt.LinasJu.Entities.Nodes.Node> getNodesFromAttributeMap(Map<String, List<Map<String, Object>>> nodesAttributes) {
        List<Map<String, Object>> nodeList = nodesAttributes.get("node");
        if (nodeList == null) {
            return null;
        }
        List<lt.LinasJu.Entities.Nodes.Node> nodeListFinal = new ArrayList<>();
        nodeList.forEach(nodeAttribute -> {
            lt.LinasJu.Entities.Nodes.Node node = new lt.LinasJu.Entities.Nodes.Node();
            node.setId(getStringFromObject(nodeAttribute, "id"));
            node.setX(getFloatFromObject(nodeAttribute, "x"));
            node.setY(getFloatFromObject(nodeAttribute, "y"));
            node.setZ(getFloatFromObject(nodeAttribute, "z"));
            node.setType(NodeTypesEnum.get(getStringFromObject(nodeAttribute, "type")));
            node.setTl(getStringFromObject(nodeAttribute, "tl"));
            nodeListFinal.add(node);
        });

        return nodeListFinal;
    }

    public List<Edge> getEdgesFromEdgeAttributes(Map<String, List<Map<String, Object>>> edgesAttributes) {
        List<Map<String, Object>> edgeList = edgesAttributes.get("edge");
        if (edgeList == null) {
            return null;
        }

        List<Edge> edgeListFinal = new ArrayList<>();
        edgeList.forEach(edgeAttribute -> {
            Edge edge = new Edge();
            edge.setId(getStringFromObject(edgeAttribute, "id"));
            edge.setFrom(getStringFromObject(edgeAttribute, "from"));
            edge.setTo(getStringFromObject(edgeAttribute, "to"));
            edge.setType(getStringFromObject(edgeAttribute, "type"));
            edge.setNumLanes(getLongFromObject(edgeAttribute, "numLanes"));
            edge.setSpeed(getFloatFromObject(edgeAttribute, "speed"));
            edge.setPriority(getLongFromObject(edgeAttribute, "priority"));
            edge.setLength(getFloatFromObject(edgeAttribute, "length"));
            edge.setShape(getShapePointListFromObject(edgeAttribute));
            edge.setSpreadType(SpreadTypeEnum.get(getStringFromObject(edgeAttribute, "spreadType")));
            edge.setAllow(getVehicleEnumListFromObject(edgeAttribute, "allow"));
            edge.setDisallow(getVehicleEnumListFromObject(edgeAttribute, "disallow"));
            edge.setWidth(getFloatFromObject(edgeAttribute, "width"));
            edge.setName(getStringFromObject(edgeAttribute, "name"));
            edge.setEndOffset(getFloatFromObject(edgeAttribute, "endOffset"));
            Float sidewalkWidth = getFloatFromObject(edgeAttribute, "sidewalkWidth");
            if (sidewalkWidth != null) {
                edge.setSidewalkWidth(sidewalkWidth);
            }
            edge.setLane(getLaneListFromObject(edgeAttribute));
            edgeListFinal.add(edge);
        });

        return edgeListFinal;
    }

    public List<Roundabout> getRoundaboutsFromAttributeMap(Map<String, List<Map<String, Object>>> edgeAttributes) {
        List<Map<String, Object>> roundaboutList = edgeAttributes.get("roundabout");
        if (roundaboutList == null) {
            return null;
        }

        List<Roundabout> roundaboutListFinal = new ArrayList<>();
        roundaboutList.forEach(roundaboutAttribute -> {
            Roundabout roundabout = new Roundabout();
            roundabout.setEdges(getStringListFromObject(roundaboutAttribute, "nodes"));
            roundabout.setNodes(getStringListFromObject(roundaboutAttribute, "edges"));
            roundaboutListFinal.add(roundabout);
        });
        return roundaboutListFinal;
    }

    public List<Type> getTypesFromAttributeMap(Map<String, List<Map<String, Object>>> typeAttributes) {
        List<Map<String, Object>> typeList = typeAttributes.get("type");
        if (typeList == null) {
            return null;
        }

        List<Type> typeListFinal = new ArrayList<>();
        typeList.forEach(typeAttribute -> {
            Type type = new Type();
            type.setId(getStringFromObject(typeAttribute, "id"));
            type.setAllow(getVehicleEnumListFromObject(typeAttribute, "allow"));
            type.setDisallow(getVehicleEnumListFromObject(typeAttribute, "disallow"));
            type.setDiscard(getBooleanFromObject(typeAttribute, "discard"));
            type.setNumLanes(getLongFromObject(typeAttribute, "numLanes"));
            type.setOneway(getBooleanFromObject(typeAttribute, "oneway"));
            type.setPriority(getLongFromObject(typeAttribute, "priority"));
            type.setSpeed(getFloatFromObject(typeAttribute, "speed"));
            type.setSidewalkWidth(getFloatFromObject(typeAttribute, "sidewalkWidth"));
            type.setRestrictions(getRestrictionListFromObject(typeAttribute));
            typeListFinal.add(type);
        });
        return typeListFinal;
    }

    public List<Connection> getConnectionsFromAttributeMap(Map<String, List<Map<String, Object>>> connectionAttributes) {
        List<Map<String, Object>> connectionList = connectionAttributes.get("connection");
        if (connectionList == null) {
            return null;
        }

        List<Connection> connectionListFinal = new ArrayList<>();
        connectionList.forEach(connectionAttribute -> {
            Connection connection = new Connection();
            connection.setFrom(getStringFromObject(connectionAttribute, "from"));
            connection.setTo(getStringFromObject(connectionAttribute, "to"));
            connection.setFromLane(getLongFromObject(connectionAttribute, "fromLane"));
            connection.setToLane(getLongFromObject(connectionAttribute, "toLane"));
            connection.setPass(getBooleanFromObject(connectionAttribute, "pass"));
            connection.setKeepClear(getBooleanFromObject(connectionAttribute, "keepClear"));

            Float contPos = getFloatFromObject(connectionAttribute, "contPos");
            if (contPos != null) {
                connection.setContPos(contPos);
            }

            Float visibility = getFloatFromObject(connectionAttribute, "visibility");
            if (visibility != null) {
                connection.setContPos(visibility);
            }

            Float speed = getFloatFromObject(connectionAttribute, "speed");
            if (speed != null) {
                connection.setSpeed(speed);
            }
            connection.setShape(getShapePointListFromObject(connectionAttribute));
            connection.setUncontrolled(getBooleanFromObject(connectionAttribute, "uncontrolled"));
            connection.setAllow(getStringListFromObject(connectionAttribute, "allow"));
            connection.setDisallow(getStringListFromObject(connectionAttribute, "disallow"));

            connection.setTl(getStringFromObject(connectionAttribute, "tl"));
            connection.setLinkIndex(getLongFromObject(connectionAttribute, "linkIndex"));
            connectionListFinal.add(connection);
        });
        return connectionListFinal;
    }

    public List<TlLogic> getTllogicsFromTllAttributeMap(Map<String, List<Map<String, Object>>> tllAttributes) {
        List<Map<String, Object>> tlLogicList = tllAttributes.get("tlLogic");
        if (tlLogicList == null) {
            return null;
        }

        List<TlLogic> tlLogicListFinal = new ArrayList<>();
        tlLogicList.forEach(connectionAttribute -> {
            TlLogic tlLogic = new TlLogic();
            tlLogic.setId(getLongFromObject(connectionAttribute, "id"));
            tlLogic.setType(TrafficLightAlgorithmType.get(getStringFromObject(connectionAttribute, "type")));
            tlLogic.setProgramID(getLongFromObject(connectionAttribute, "programID"));
            tlLogic.setOffset(getLongFromObject(connectionAttribute, "offset"));
            tlLogic.setPhase(getPhaseListFromObject(connectionAttribute));

            tlLogicListFinal.add(tlLogic);
        });

        return tlLogicListFinal;
    }

    private List<Lane> getLaneListFromObject(Map<String, Object> attributes) {
        Object object = attributes.get("lane");
        if (object == null) {
            return null;
        }

        if (!(object instanceof ArrayList)) {
            return null;
        }

        ArrayList<Map<String, Object>> lanePropertiesMap = (ArrayList<Map<String, Object>>) object;
        List<Lane> lanes = new ArrayList<>();
        lanePropertiesMap.stream().map(this::getLaneFromMap).forEach(lanes::add);
        return lanes;

    }

    private Lane getLaneFromMap(Map<String, Object> laneMap) {
        Lane lane = new Lane();
        lane.setIndex(getLongFromObject(laneMap, "index"));
        lane.setAllow(getStringListFromObject(laneMap, "allow"));
        lane.setDisallow(getStringListFromObject(laneMap, "disallow"));
        lane.setSpeed(getFloatFromObject(laneMap, "speed"));
        lane.setWidth(getFloatFromObject(laneMap, "width"));
        lane.setEndOffset(getFloatFromObject(laneMap, "endOffset"));
        lane.setShape(getShapePointListFromObject(laneMap));
        lane.setAcceleration(getLongFromObject(laneMap, "acceleration"));
        return lane;
    }

    private List<Restriction> getRestrictionListFromObject(Map<String, Object> attributes) {
        Object object = attributes.get("restriction");
        if (object == null) {
            return null;
        }

        return null; //todo grazinti reikalinga lista, kol kas nenaudojamas
    }

    private boolean getBooleanFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) != null && (Objects.equals(String.valueOf(attributes.get(key)), "1"));
    }

    private String getStringFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
    }

    private Float getFloatFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : Float.parseFloat(String.valueOf(attributes.get(key)));
    }

    private Long getLongFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : Long.parseLong(String.valueOf(attributes.get(key)));
    }

    private List<ShapePoint> getShapePointListFromObject(Map<String, Object> attributes) {
        List<String> shapePointStringList = getStringListFromObject(attributes, "shape"); //separated by spaces different shape points
        if (shapePointStringList == null) {
            return null;
        }

        List<ShapePoint> shapePoints = new ArrayList<>();

        shapePointStringList.forEach(shapePointString -> {
            List<String> shapePointsSeparated = Arrays.asList(shapePointString.split(","));
            int listSize = shapePointsSeparated.size();
            ShapePoint shapePoint = new ShapePoint();
            shapePoint.setX(Float.parseFloat(shapePointsSeparated.get(0)));
            shapePoint.setY(Float.parseFloat(shapePointsSeparated.get(1)));
            shapePoint.setZ(listSize > 2 ? Float.parseFloat(shapePointsSeparated.get(2)) : null);
            shapePoints.add(shapePoint);
        });

        return shapePoints;
    }

    private List<String> getStringListFromObject(Map<String, Object> attributes, String key) {
        String parsedString = getStringFromObject(attributes, key);
        return parsedString == null ? null : new ArrayList<>(Arrays.asList(parsedString.split(" ")));
    }

    private List<Integer> getIntegerlistFromObject(Map<String, Object> attributes, String key) {
        List<String> parsedStringList = getStringListFromObject(attributes, key);
        if (parsedStringList == null || parsedStringList.isEmpty()) {
            return null;
        }

        return parsedStringList.stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    private List<VehicleClassEnum> getVehicleEnumListFromObject(Map<String, Object> attributes, String key) {
        List<String> stringList = getStringListFromObject(attributes, key);
        if (stringList == null) {
            return null;
        }

        return stringList.stream().map(VehicleClassEnum::get).collect(Collectors.toList());
    }

    private List<Phase> getPhaseListFromObject(Map<String, Object> attributes) {
        Object object = attributes.get("phase");
        if (object == null) {
            return null;
        }

        if (!(object instanceof ArrayList)) {
            return null;
        }

        ArrayList<Map<String, Object>> phasePropertiesMap = (ArrayList<Map<String, Object>>) object;
        return phasePropertiesMap.stream().map(this::getPhaseFromMap).collect(Collectors.toList());
    }

    private Phase getPhaseFromMap(Map<String, Object> laneMap) {
        Phase phase = new Phase();
        phase.setDuration(getLongFromObject(laneMap, "duration"));
        phase.setState(getStateEnumList(laneMap));
        phase.setMinDur(getLongFromObject(laneMap, "minDur"));
        phase.setMaxDur(getLongFromObject(laneMap, "maxDur"));
        phase.setName(getStringFromObject(laneMap, "name"));
        return phase;
    }

    private List<SignalStateEnum> getStateEnumList(Map<String, Object> laneMap) {
        String stringState = getStringFromObject(laneMap, "state");
        if (stringState == null) {
            return null;
        }

        List<SignalStateEnum> states = new ArrayList<>();
        for (int i  = 0; i < stringState.length(); i++) {
            states.add(SignalStateEnum.get(String.valueOf(stringState.charAt(i))));
        }
        return states;
    }
}
