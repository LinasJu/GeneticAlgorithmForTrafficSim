package lt.LinasJu;

import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Edges.Lane;
import lt.LinasJu.Entities.Edges.SpreadTypeEnum;
import lt.LinasJu.Entities.Nodes.NodeTypesEnum;
import lt.LinasJu.Entities.Nodes.ShapePoint;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ParserRepo {
    /**
     *
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

    public List<lt.LinasJu.Entities.Nodes.Node> getNodesFromNodesAttributes(Map<String, List<Map<String, Object>>> nodesAttributes) {
        List<Map<String, Object>> nodeList  = nodesAttributes.get("node");
        List<lt.LinasJu.Entities.Nodes.Node> nodeListFinal = new ArrayList<>();
        nodeList.forEach(nodeAttributes -> {
            lt.LinasJu.Entities.Nodes.Node node = new lt.LinasJu.Entities.Nodes.Node();
            node.setId(getStringFromObject(nodeAttributes, "id"));
            node.setX(getFloatFromObject(nodeAttributes, "x"));
            node.setY(getFloatFromObject(nodeAttributes, "y"));
            node.setType(NodeTypesEnum.get(getStringFromObject(nodeAttributes, "type")));
            node.setTl(getStringFromObject(nodeAttributes, "tl"));
            nodeListFinal.add(node);
        });

        return nodeListFinal;
    }

    public List<Edge> getEdgesFromEdgesAttributes(Map<String, List<Map<String, Object>>> edgesAttributes) {
        List<Map<String, Object>> nodeList  = edgesAttributes.get("edge");
        List<Edge> edgeListFinal = new ArrayList<>();
        nodeList.forEach(nodeAttributes -> {
            Edge edge = new Edge();
            edge.setId(getStringFromObject(nodeAttributes, "id"));
            edge.setFrom(getStringFromObject(nodeAttributes, "from"));
            edge.setTo(getStringFromObject(nodeAttributes, "to"));
            edge.setType(getStringFromObject(nodeAttributes, "type"));
            edge.setNumLanes(getIntegerFromObject(nodeAttributes, "numLanes"));
            edge.setSpeed(getFloatFromObject(nodeAttributes, "speed"));
            edge.setPriority(getIntegerFromObject(nodeAttributes, "priority"));
            edge.setShape(getShapePointListFromObject(nodeAttributes, "shape"));
            edge.setSpreadTypeEnum(SpreadTypeEnum.get(getStringFromObject(nodeAttributes, "spreadType")));
            edge.setAllow(getStringListFromObject(nodeAttributes, "allow"));
            edge.setDisallow(getStringListFromObject(nodeAttributes, "disallow"));
            edge.setWidth(getFloatFromObject(nodeAttributes, "width"));
            edge.setName(getStringFromObject(nodeAttributes, "name"));
            edge.setEndOffset(getFloatFromObject(nodeAttributes, "endOffset"));
            edge.setSidewalkWidth(getFloatFromObject(nodeAttributes, "sidewalkWidth"));
            edge.setLanes(getLaneListFromObject(nodeAttributes, "lane"));
            edgeListFinal.add(edge);
        });

        return edgeListFinal;
    }

    private List<Lane> getLaneListFromObject(Map<String, Object> attributes, String key) {
        Object object = attributes.get(key);
        if (object == null) {
            return null;
        }

        if (object instanceof ArrayList) {
            ArrayList<Map<String, Object>> LanePropertiesMap = (ArrayList<Map<String, Object>>) object;
            List<Lane> lanes = new ArrayList<>();
            LanePropertiesMap.stream().map(this::getLaneFromMap).forEach(lanes::add);
            return lanes;
        }

        return null;
    }

    private Lane getLaneFromMap(Map<String, Object> laneMap) {
        Lane lane = new Lane();
        lane.setIndex(getIntegerFromObject(laneMap, "index"));
        lane.setAllow(getStringListFromObject(laneMap, "allow"));
        lane.setDisallow(getStringListFromObject(laneMap, "disallow"));
        lane.setSpeed(getFloatFromObject(laneMap, "speed"));
        lane.setWidth(getFloatFromObject(laneMap, "width"));
        lane.setEndOffset(getFloatFromObject(laneMap, "endOffset"));
        lane.setShape(getShapePointListFromObject(laneMap, "shape"));
        lane.setAcceleration(getFloatFromObject(laneMap, "acceleration"));
        return lane;
    }

    private String getStringFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
    }

    private Float getFloatFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : Float.parseFloat(String.valueOf(attributes.get(key)));
    }

    private Integer getIntegerFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : Integer.parseInt(String.valueOf(attributes.get(key)));
    }

    private List<ShapePoint> getShapePointListFromObject(Map<String, Object> attributes, String key) {
        List<String> shapePointStringList = getStringListFromObject(attributes, key); //separated by spaces different shape points
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


}
