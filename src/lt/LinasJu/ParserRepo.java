package lt.LinasJu;

import lt.LinasJu.Entities.Edges.Edge;
import lt.LinasJu.Entities.Nodes.NodeTypesEnum;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return null; // todo
    }

    private String getStringFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : String.valueOf(attributes.get(key));
    }

    private Float getFloatFromObject(Map<String, Object> attributes, String key) {
        return attributes.get(key) == null ? null : Float.parseFloat(String.valueOf(attributes.get(key)));
    }

}
